package scroll.benchmarks

import scroll.internal.Compartment
import scroll.internal.graph.ScalaRoleGraphBuilder

class TestCompartment(params: BenchParams) extends Compartment {

	ScalaRoleGraphBuilder.jastAdd(params.useJastAdd)

	case class ParamRole(p: Int) {
		val param: Int = p

		def getParam: Int = {
			param
		}
	}

	case class ParamRoleA(p: Int) {
		val param: Int = p

		def getParam: Int = {
			param
		}

		def a(): Int = {
			param
		}
	}

	case class ParamRoleB(p: Int) {
		val param: Int = p

		def getParam: Int = {
			param
		}

		def b(): Int = {
			param
		}
	}

	val roles = new Array[ParamRole](params.nrRoles)
	val naturals = new Array[Copter](params.nrOfNaturals)

	def createRoles(): Unit = {
		for(a <- 0 until params.nrRoles) {
			roles(a) = ParamRole(a)
		}
	}

	def bindRoles(): Unit = {
		for(n <- 0 until params.nrOfNaturals) {
			naturals(n) = new Copter("Copter = " + n)
			naturals(n) <+> roles(n * params.nrRolesPerNatural)
			var i = 1
			for(a <- 1 until params.nrLevels) {
				for(j <- 0 until scala.math.pow(2, a).toInt) {
					val parent: Int = a - 1
					roles((n * params.nrRolesPerNatural) + parent) <+> roles((n * params.nrRolesPerNatural) + i)
					i += 1
				}
			}
		}
	}

	def unbindRoles(): Unit = {
		for(n <- 0 until params.nrOfNaturals) {
			naturals(n) <-> roles(n * params.nrRolesPerNatural)
			var i = 1
			for(a <- 1 until params.nrLevels) {
				for(j <- 0 until scala.math.pow(2, a).toInt) {
					val parent: Int = a - 1
					roles((n * params.nrRolesPerNatural) + parent) <-> roles((n * params.nrRolesPerNatural) + i)
					i += 1
				}
			}
		}
	}

	def dispatchRoles(nrOfDispatchesPerNatural: Int): Unit = {
		for(_ <- 0 until nrOfDispatchesPerNatural) {
			var output = ""
			for(n <- 0 until params.nrOfNaturals) {
				output += "val: " + (+naturals(n)).getParam() + "\n"
			}
			output
		}
	}

	def dispatchAndUpdateUnrelated(): Unit = {
		var output = ""
		output += "val: " + (+naturals(5)).getParam() + "\n"
		output += "val: " + (+naturals(5)).getParam() + "\n"
		output += "val: " + (+naturals(5)).getParam() + "\n"
		output += "val: " + (+naturals(5)).getParam() + "\n"

		//removed and rebind role from natural 0
		val parent: Int = scala.math.pow(2, params.nrLevels - 1).toInt - 2
		roles(parent) <-> roles((params.nrLevels - 1) * (params.nrLevels - 1))
		roles(parent) <+> roles((params.nrLevels - 1) * (params.nrLevels - 1))
	}

	def dispatchAndUpdateRelated(): Unit = {
		var output = ""
		this.dispatchAndUpdateUnrelated()
		output += "val: " + (+naturals(0)).getParam() + "\n" //should trigger incremental update
	}
}

class Copter(name: String) {}


