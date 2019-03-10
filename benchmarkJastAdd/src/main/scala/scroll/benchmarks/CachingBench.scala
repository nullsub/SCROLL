package scroll.benchmarks

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import scroll.internal.Compartment
import scroll.internal.graph.ScalaRoleGraphBuilder

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
class CachingBench {


	class Copter(name: String) {}

	class SomeCompartment(useJastAdd: Boolean) extends Compartment {

		ScalaRoleGraphBuilder.jastAdd(useJastAdd)

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

		def run(params: BenchParams): Unit = {
			val roles = new Array[ParamRole](params.nrRoles)
			val naturals = new Array[Copter](params.nrOfNaturals)

			//createRoles
			for(a <- 0 until params.nrRoles) {
				roles(a) = ParamRole(a)
			}

			//bind roles
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

			for(n <- 0 until params.nrOfDispatchesPerNatural) {
				var output = ""
				for(n <- 0 until params.nrOfNaturals) {
					output += "val: " + (+naturals(n)).getParam() + "\n"
				}
				output
			}
		}
	}

	@Benchmark
	def runBenchmark(params: BenchParams): Unit = {
		val c = new SomeCompartment(params.useJastAdd)
		c.run(params)
	}
}


@State(Scope.Thread)
class BenchParams {
	@Param(Array("true", "false"))
	var useJastAdd: Boolean = _

	@Param(Array("50", "100", "200", "500", "800", "1000", "2000"))
	var nrOfDispatchesPerNatural: Int = _

	//relevant for measuring creation time
	@Param(Array("1000"))
	var nrOfNaturals: Int = _

	@Setup
	def setup(): Unit = {
		nrLevels = 8
		nrRolesPerNatural = scala.math.pow(2, nrLevels + 1).toInt - 1
		nrRoles = nrRolesPerNatural * nrOfNaturals
	}

	var nrLevels: Int = _
	var nrRolesPerNatural: Int = _
	var nrRoles: Int = _
}
