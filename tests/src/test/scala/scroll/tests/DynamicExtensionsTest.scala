package scroll.tests

import scroll.tests.mocks.CoreA

import java.{util => ju, lang => jl}
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

@RunWith(value = classOf[Parameterized])
class DynamicExtensionsTest(cached: Boolean) extends AbstractSCROLLTest(cached) {

  @Test
  def testAddingDynamicExtensions(): Unit = {
    val someCore = new CoreA()
    new CompartmentUnderTest() {
      val someRole = new RoleA()
      someCore <+> someRole
      someCore <+> new RoleB()

      someCore <-> someRole

      someCore a()
      +someCore a()

      assertFalse((+someCore).hasExtension[RoleA])
      assertTrue((+someCore).hasExtension[RoleB])

      val resB: String = +someCore b()
      assertEquals("b", resB)
    }
  }

}

object DynamicExtensionsTest {
    @Parameters
    def parameters: ju.Collection[Array[jl.Boolean]] = {
        val list = new ju.ArrayList[Array[jl.Boolean]]()
        list.add(Array(true))
        list.add(Array(false))
        list
    }
}
