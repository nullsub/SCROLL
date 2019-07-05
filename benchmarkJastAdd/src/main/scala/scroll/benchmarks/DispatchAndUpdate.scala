package scroll.benchmarks

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
class DispatchAndUpdate {
/*
	@Benchmark
	def dispatchAndUpdateUnrelated(params: DispatchAndUpdateParams): Unit = {
		for(_ <- 0 until params.nrOfDispatches) {
			params.c.dispatchAndUpdateUnrelated(params.dispatchesPerCycle)
		}
	}

	@Benchmark
	def dispatchAndUpdateRelated(params: DispatchAndUpdateParams): Unit = {
		for(_ <- 0 until params.nrOfDispatches) {
			params.c.dispatchAndUpdateRelated(params.dispatchesPerCycle)
		}
	}
	*/
}

@State(Scope.Thread)
class DispatchAndUpdateParams extends BenchParams {
	@Param(Array(
		"50", "100", "200", "300", "400", "500", "600", "700", "800", "900", "1000", "1500", "2000"
	))
	var nrOfDispatches: Int = _

	@Param(Array(
		"5"
	))
	var dispatchesPerCycle: Int = _

	@Param(Array("500"))
	var nrOfNaturals: Int = _

	@Setup
	override def setup(): Unit = {
		super.setup()
		c.bindRoles()
	}
}

