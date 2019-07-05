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

			def test(): Unit = println("in testing...")

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

		final val nrOfNaturals = 1000
		final val nrLevels: Integer = 8
		final val nrRolesPerNatural: Integer = scala.math.pow(2, nrLevels + 1).toInt - 1
		final val nrRoles: Integer = nrRolesPerNatural * nrOfNaturals
		var roles = new Array[ParamRole](nrRoles)
		var naturals = new Array[Copter](nrRoles)

		//createRoles
		for(a <- 0 until nrRoles) {
			roles(a) = ParamRole(a)
		}

		//bind roles
		for(n <- 0 until nrOfNaturals) {
			naturals(n) = Copter("Copter = " + n)
			naturals(n) <+> roles(n * nrRolesPerNatural)
			var i = 1
			for(a <- 1 until nrLevels) {
				for(j <- 0 until scala.math.pow(2, a).toInt) {
					val parent: Integer = a - 1
					//println("a = " + a + " j = " + j + " 2^a = " + scala.math.pow(2, a).toInt + " i = " + i)
					roles((n * nrRolesPerNatural) + parent) <+> roles((n * nrRolesPerNatural) + i)
					i += 1
				}
			}
		}


		for(n <- 0 until 2000) {
			//println("doing dispatch: " + n)
			this.dispatchAll()
		}
		naturals(0) <+> new SimpleAPI()
		(+naturals(0)).fly()
		(+naturals(0)).test()


		println("finished")

		private def dispatchAll(): String = {
			var output = ""
			//traverse roles and dispatch every role
			for(n <- 0 until nrOfNaturals) {
				output += "val: " + (+naturals(n)).getParam() + "\n"
			}
			output
		}
	}
}
