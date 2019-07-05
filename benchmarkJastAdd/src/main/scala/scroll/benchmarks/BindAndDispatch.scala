package scroll.benchmarks

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
class BindAndDispatch {
/*
	 //same performance for every implementation
	@Benchmark
	def bindAndDispatchSimple(params: BindAndDispatchParams): Unit = {
		params.c.bindRoles()
		params.c.dispatchRolesSimple(params.nrOfDispatchesPerNatural)
	}

	@Benchmark
	def bindAndDispatchLongTraversal(params: BindAndDispatchParams): Unit = {
		params.c.bindRoles()
		params.c.dispatchRolesLongTraversal(params.nrOfDispatchesPerNatural)
	}*/

}

@State(Scope.Thread)
class BindAndDispatchParams extends BenchParams {
	@Param(Array(
		"1", "200", "750", "1500", "2000", "3000", "4000"
	))
	var nrOfDispatchesPerNatural: Int = _

	@Param(Array("500"))
	var nrOfNaturals: Int = _
}

