package scroll.benchmarks

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
class DispatchAndUpdateParametrized {
/*
	@Benchmark
	def dispatchAndUpdateUnrelatedWithDQ(params: DispatchAndUpdateParametrizedParams): Unit = {
		for(_ <- 0 until params.nrOfDispatches) {
			params.c.dispatchAndUpdateUnrelatedWithDQ(params.dispatchesPerCycle)
		}
	}

	@Benchmark
	def dispatchAndUpdateUnrelated(params: DispatchAndUpdateParametrizedParams): Unit = {
		for(_ <- 0 until params.nrOfDispatches) {
			params.c.dispatchAndUpdateUnrelated(params.dispatchesPerCycle)
		}
	}

	@Benchmark
	def dispatchAndUpdateRelated(params: DispatchAndUpdateParametrizedParams): Unit = {
		for(_ <- 0 until params.nrOfDispatches) {
			params.c.dispatchAndUpdateRelated(params.dispatchesPerCycle)
		}
	}
	*/

}

@State(Scope.Thread)
class DispatchAndUpdateParametrizedParams extends BenchParams {
	@Param(Array(
		"2000"
	))
	var nrOfDispatches: Int = _

	@Param(Array(
		"1", "2", "3", "4", "5", "7", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75",
		"80", "85", "90", "95", "100", "110", "120", "130", "140", "150", "160", "170", "180", "190", "200"
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

