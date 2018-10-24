package de.tud.deussen.scala

import de.tud.deussen.jastadd.gen._
import scroll.internal.Compartment

object SCROLLBoot extends App {

	case class Copter(name: String)

	class SomeCompartment extends Compartment {

		case class SimpleAPI() {
			def fly(): Unit = {
				val name: String = (+this).name
				println(name + " is flying ...")
			}
		}

		case class EmergencyDetector() {

			def isFalling: Boolean = true // faked

			def fly(): Unit = {
				val name: String = (+this).name
				println(name + " is flying safely ...")
			}
		}

		case class Parachute() {
			def openParachute(): Unit = {
				println("Opening ... ")
			}
		}

	}

	new SomeCompartment {

		val dji = Copter("DJIPhantom") <+> SimpleAPI() <+> EmergencyDetector() <+> Parachute()

		/*
		   implicit val dd =
		   From(_.isInstanceOf[Copter]).
		   To(_.isInstanceOf[EmergencyDetector]).
		   Through(anything).
		   Bypassing(_.isInstanceOf[SimpleAPI])
		 */

		dji.fly()

		val falling: Boolean = dji.isFalling()
		if(falling) {
			dji.openParachute()
		}

		var ast = new AstCreator
		var someRoot: Root = ast.createAst
		expVisitor(someRoot.getExp)
		println()
	}

	def expVisitor(exp: Exp, intend: Int = 0): Unit = {
		val intendChar = "      "
		println()
		print(intendChar * intend + exp.getClass.getName)
		exp match {
			case i: Number =>
				print(": " + i.getValue)
			case v: Var =>
				print(": " + v.getName)
			case _ =>
				if(exp.getNumChild == 0)
					print(intendChar * intend + "unknown exp type")
				for(x <- 0 until exp.getNumChild)
					expVisitor(exp.getChild(x).asInstanceOf[Exp], intend + 1)
		}
	}

}

