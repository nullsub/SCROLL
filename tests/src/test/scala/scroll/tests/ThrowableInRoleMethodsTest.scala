package scroll.tests


import java.{util => ju, lang => jl}
import org.junit.Test
import org.junit.Assert.fail
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

import java.io.IOException

import scroll.internal.Compartment

@RunWith(value = classOf[Parameterized])
class ThrowableInRoleMethodsTest(cached: Boolean) extends AbstractSCROLLTest(cached) {

  class CoreType

  class ExceptionShowcase extends CompartmentUnderTest {

    class Exceptional {
      def roleMethodWithError(): Unit = {
        throw new Error()
      }

      def roleMethodWithUncheckedException(): Unit = {
        throw new RuntimeException()
      }

      def roleMethodWithCheckedException(): Unit = {
        throw new IOException()
      }
    }

  }

  @Test
  def testErrorInRoleMethod(): Unit = {
    new ExceptionShowcase() {
      val core = new CoreType()
      core play new Exceptional()
      try {
        (+core).roleMethodWithError()
        fail("Should throw an Error")
      } catch {
        case _: Error => // all good
      }
    }
  }

  @Test
  def testUncheckedExceptionInRoleMethod(): Unit = {
    new ExceptionShowcase() {
      val core = new CoreType()
      core play new Exceptional()
      try {
        (+core).roleMethodWithUncheckedException()
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }
    }
  }

  @Test
  def testCheckedExceptionInRoleMethod(): Unit = {
    new ExceptionShowcase() {
      val core = new CoreType()
      core play new Exceptional()
      try {
        (+core).roleMethodWithCheckedException()
        fail("Should throw an IOException")
      } catch {
        case _: IOException => // all good
      }
    }
  }

}

object ThrowableInRoleMethodsTest {
    @Parameters
    def parameters: ju.Collection[Array[jl.Boolean]] = {
        val list = new ju.ArrayList[Array[jl.Boolean]]()
        list.add(Array(true))
        list.add(Array(false))
        list
    }
}
