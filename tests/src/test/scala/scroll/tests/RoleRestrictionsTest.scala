package scroll.tests

import java.{util => ju, lang => jl}
import org.junit.Test
import org.junit.Assert.fail
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

import mocks.CoreA

@RunWith(value = classOf[Parameterized])
class RoleRestrictionsTest(cached: Boolean) extends AbstractSCROLLTest(cached) {

  @Test
  def testRoleRestrictionValidation(): Unit = {
    val player = new CoreA()
    new CompartmentUnderTest() {
      val roleA = new RoleA()
      val roleD = new RoleD()
      AddRoleRestriction[CoreA, RoleA]()
      player play roleA
      player drop roleA
      ReplaceRoleRestriction[CoreA, RoleD]()
      try {
        player play roleA
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }
    }
  }

  @Test
  def testRoleRestrictionValidationOnMultipleTypes(): Unit = {
    val player = new CoreA()
    new CompartmentUnderTest() {
      val roleA = new RoleA()
      val roleD = new RoleD()
      AddRoleRestriction[CoreA, RoleA]()
      AddRoleRestriction[CoreA, RoleD]()
      player play roleA
      player play roleD
      try {
        player play new RoleB()
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }
    }
  }

  @Test
  def testRoleRestrictionValidationAfterRemoval(): Unit = {
    val player = new CoreA()
    new CompartmentUnderTest() {
      val roleA = new RoleA()
      val roleD = new RoleD()
      AddRoleRestriction[CoreA, RoleA]()
      player play roleA
      RemoveRoleRestriction[CoreA]()
      player play roleD
      player drop roleA drop roleD
      AddRoleRestriction[CoreA, RoleA]()
      AddRoleRestriction[CoreA, RoleD]()
      player play roleA play roleD
      player drop roleA drop roleD
      RemoveRoleRestriction[CoreA]()
      player play roleA play roleD
    }
  }
}

object RoleRestrictionsTest {
    @Parameters
    def parameters: ju.Collection[Array[jl.Boolean]] = {
        val list = new ju.ArrayList[Array[jl.Boolean]]()
        list.add(Array(true))
        list.add(Array(false))
        list
    }
}
