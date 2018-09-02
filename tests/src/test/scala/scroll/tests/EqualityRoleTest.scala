package scroll.tests

import mocks.CoreA
import java.{util => ju, lang => jl}
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotEquals

@RunWith(value = classOf[Parameterized])
class EqualityRoleTest(cached: Boolean) extends AbstractSCROLLTest(cached) {

  @Test
  def testRoleEquality(): Unit = {
    val someCore = new CoreA()
    new CompartmentUnderTest() {
      val someRole = new RoleA()
      val player = someCore play someRole

      assertEquals(player, player)
      assertEquals(someCore, someCore)
      assertEquals(player, someCore)
      assertEquals(+player, player)
      assertEquals(player, +player)
      assertEquals(someRole, someRole)
      assertEquals(+someRole, player)
      assertEquals(player, +someRole)
      assertEquals(+someRole, someCore)
    }
  }

  @Test
  def testRoleEqualityChainedDeepRoles(): Unit = {
    val someCore = new CoreA()
    new CompartmentUnderTest() {
      val someRole = new RoleA()
      val someOtherRole = new RoleB()
      val player = (someCore play someRole) play someOtherRole

      assertEquals(player, player)
      assertEquals(someCore, someCore)
      assertEquals(player, someCore)
      assertEquals(+player, player)
      assertEquals(player, +player)
      assertEquals(someRole, someRole)
      assertEquals(someOtherRole, someOtherRole)
      assertNotEquals(someRole, someOtherRole)

      val a = +someRole
      val b = +someOtherRole
      assertEquals(a, player)
      assertEquals(player, a)
      assertEquals(b, player)
      assertEquals(player, b)
      assertEquals(+someRole, someCore)
      assertEquals(+someOtherRole, someCore)
    }
  }

  @Test
  def testRoleEqualitySeparateDeepRoles(): Unit = {
    val someCore = new CoreA()
    new CompartmentUnderTest() {
      val someRole = new RoleA()
      val someOtherRole = new RoleB()
      val player = someCore play someRole
      someRole play someOtherRole

      assertEquals(player, player)
      assertEquals(someCore, someCore)
      assertEquals(player, someCore)

      assertEquals(+player, player)
      assertEquals(player, +player)

      assertEquals(someRole, someRole)
      assertEquals(someOtherRole, someOtherRole)

      assertNotEquals(someRole, someOtherRole)

      val a = +someRole
      val b = +someOtherRole
      assertEquals(a, player)
      assertEquals(player, a)
      assertEquals(b, player)
      assertEquals(player, b)

      assertEquals(+someRole, someCore)
      assertEquals(+someOtherRole, someCore)
    }
  }

}

object EqualityRoleTest {
    @Parameters
    def parameters: ju.Collection[Array[jl.Boolean]] = {
        val list = new ju.ArrayList[Array[jl.Boolean]]()
        list.add(Array(true))
        list.add(Array(false))
        list
    }
}
