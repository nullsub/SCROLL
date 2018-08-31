package scroll.tests

import org.junit.Assert.fail
import org.junit.Test
import scroll.tests.mocks.CoreA

class RoleGroupsTest(cached: Boolean) extends AbstractSCROLLTest(cached) {

  class Source

  class Target

  @Test
  def testValidation(): Unit = {
    val acc1 = new CoreA()
    val acc2 = new CoreA()
    new SomeCompartment() {
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
