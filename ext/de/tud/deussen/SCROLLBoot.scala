package de.tud.deussen

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


		case class ParamRole(p: Integer) {
			val param: Integer = p
			def getParam(): Integer = {
				param
			}
		}
	}

	new SomeCompartment {

		var dji = Copter("DJIPhantom") <+> SimpleAPI() <+> EmergencyDetector() <+> Parachute()


/*
		dji.fly()

		val falling: Boolean = dji.isFalling()
		if(falling) {
			dji.openParachute()
		}
*/
		final val nrLevels: Integer = 11
		final val nrRoles: Integer = scala.math.pow(2, nrLevels + 1).toInt - 1
		var roles = new Array[ParamRole](nrRoles)

		//createRoles
		for( a <- 0 until nrRoles){
			roles(a) = ParamRole(a)
		}

		//bind roles
		dji play roles(0)
		var i = 1
		for( a <- 1 until nrLevels) {
			for( j <- 0 until scala.math.pow(2, a).toInt) {
				val parent: Integer = a-1
				//println("a = " + a + " j = " + j + " 2^a = " + scala.math.pow(2, a).toInt + " i = " + i)
				roles(parent) <+> roles(i)
				i += 1
			}
		}

		println("doing first dispatch")
		//traverse roles and dispatch every role
		i = 1
		for( a <- 1 until nrLevels) {
			for( j <- 0 until scala.math.pow(2, a).toInt) {
				//val parent: Integer = a-1
				(+roles(i)).getParam()
				//println("a = " + a + " j = " + j + " 2^a = " + scala.math.pow(2, a).toInt + " i = " + i + " param = " + (+roles(i)).getParam())
				i += 1
			}
		}

		println("doing second dispatch")
		//traverse roles and dispatch every role
		i = 1
		for( a <- 1 until nrLevels) {
			roles(a-1) play Parachute()
			for( j <- 0 until scala.math.pow(2, a).toInt) {
				//val parent: Integer = a-1
				(+roles(i)).getParam()
				//println("a = " + a + " j = " + j + " 2^a = " + scala.math.pow(2, a).toInt + " i = " + i + " param = " + (+roles(i)).getParam())
				i += 1
			}
		}
		println("finished")
	}
}
