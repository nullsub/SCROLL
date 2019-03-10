package scroll.benchmarks

import org.openjdk.jmh.annotations.{Param, Scope, Setup, State}

@State(Scope.Thread)
class BenchParams {
	@Param(Array("true", "false"))
	var useJastAdd: Boolean = _

	@Param(Array("1000"))
	var nrOfNaturals: Int = _

	@Setup
	def setup(): Unit = {
		nrLevels = 8
		nrRolesPerNatural = scala.math.pow(2, nrLevels + 1).toInt - 1
		nrRoles = nrRolesPerNatural * nrOfNaturals
		c = new TestCompartment(this)
		c.createRoles()
	}

	var nrLevels: Int = _
	var nrRolesPerNatural: Int = _
	var nrRoles: Int = _

	var c: TestCompartment = _
}


