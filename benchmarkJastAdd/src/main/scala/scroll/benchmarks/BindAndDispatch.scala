package scroll.benchmarks

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
class CreateAndDispatch {
/*
	@Benchmark
	def bindAndDispatchSimple(params: BindAndDispatchParams): Unit = {
		params.c.bindRoles()
		params.c.dispatchRolesSimple(params.nrOfDispatchesPerNatural)
	}

	@Benchmark
	def bindAndDispatchLongTraversal(params: BindAndDispatchParams): Unit = {
		params.c.bindRoles()
		params.c.dispatchRolesLongTraversal(params.nrOfDispatchesPerNatural)
	}
*/
}

@State(Scope.Thread)
class BindAndDispatchParams extends BenchParams {
	@Param(Array(
		"1", "200", "750", "1500", "2000", "3000", "4000"
	))
	var nrOfDispatchesPerNatural: Int = _

	@Param(Array("1000"))
	var nrOfNaturals: Int = _
}

