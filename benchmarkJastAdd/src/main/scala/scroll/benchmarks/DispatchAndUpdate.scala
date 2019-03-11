package scroll.benchmarks

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
class DispatchAndUpdate {
/*
	@Benchmark
	def dispatchAndUpdateUnrelated(params: DispatchAndUpdateParams): Unit = {
		for(_ <- 0 until params.nrOfDispatches) {
			params.c.dispatchAndUpdateUnrelated()
		}
	}

	@Benchmark
	def dispatchAndUpdateRelated(params: DispatchAndUpdateParams): Unit = {
		for(_ <- 0 until params.nrOfDispatches) {
			params.c.dispatchAndUpdateRelated()
		}
	}
	*/
}

@State(Scope.Thread)
class DispatchAndUpdateParams extends BenchParams {
	@Param(Array(
		"1000", "1500", "2000", "3000", "4000"
	))
	var nrOfDispatches: Int = _

	@Param(Array("1000"))
	var nrOfNaturals: Int = _

	@Setup
	override def setup(): Unit = {
		super.setup()
		c.bindRoles()
	}
}

