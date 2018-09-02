package scroll.tests

import org.junit.Assert.fail
import java.{util => ju, lang => jl}
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

import scroll.tests.mocks.CoreA

@RunWith(value = classOf[Parameterized])
class RoleGroupsTest(cached: Boolean) extends AbstractSCROLLTest(cached) {

  class Source

  class Target

  @Test
  def testValidation(): Unit = {
    val acc1 = new CoreA()
    val acc2 = new CoreA()
    new CompartmentUnderTest() {
      val source = new Source
      val target = new Target

      val transaction: RoleGroup = RoleGroup("Transaction").containing[Source, Target](1, 1)(2, 2)

      RoleGroupsChecked {
        acc1 play source
        acc2 play target
      }

      try {
        RoleGroupsChecked {
          acc2 drop target
        }
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }

      try {
        RoleGroupsChecked {
          acc1 play target
        }
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }
    }
  }
}

object RoleGroupsTest {
    @Parameters
    def parameters: ju.Collection[Array[jl.Boolean]] = {
        val list = new ju.ArrayList[Array[jl.Boolean]]()
        list.add(Array(true))
        list.add(Array(false))
        list
    }
}
