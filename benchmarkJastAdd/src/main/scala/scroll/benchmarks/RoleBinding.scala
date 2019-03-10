package scroll.benchmarks

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
class RoleBinding {

	@Benchmark
	def bindRoles(params: RoleBindingParams): Unit = {
		params.c.bindRoles()
	}
}

@State(Scope.Thread)
class RoleBindingParams extends BenchParams {

}
