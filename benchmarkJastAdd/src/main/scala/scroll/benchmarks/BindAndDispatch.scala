package scroll.benchmarks

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
class CreateAndDispatch {

	@Benchmark
	def bindAndDispatch(params: BindAndDispatchParams): Unit = {
		params.c.bindRoles()
		params.c.dispatchRoles(params.nrOfDispatchesPerNatural)
	}
}

@State(Scope.Thread)
class BindAndDispatchParams extends BenchParams {

	@Param(Array(
		"1", "50", "100", "150", "200", "250", "300",
		"350", "400", "500", "600",
		"700", "1000", "1500", "2000",
	))
	var nrOfDispatchesPerNatural: Int = _
}

