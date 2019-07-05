package scroll.benchmarks

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
class RoleBinding {


	@Benchmark
	def bindRoles(params: RoleBindingParams): Unit = {
		params.c.bindRoles()
	}

	/*
		//does not work. When removing roles, removed role is added to root causing oom. JastAddGraph:158
	@Benchmark
	def unbindRoles(params: RoleBindingWithBindParams): Unit = {
		params.c.unbindRoles()
	}
	*/
}

@State(Scope.Thread)
class RoleBindingParams extends BenchParams {

	@Param(Array("100", "400", "600", "1000", "1500", "2000"))
	var nrOfNaturals: Int = _
}

@State(Scope.Thread)
class RoleBindingWithBindParams extends RoleBindingParams {

	@Setup
	override def setup(): Unit = {
		super.setup()
		this.c.bindRoles()
	}

}

