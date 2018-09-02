package scroll.tests

import scroll.examples._

import java.{util => ju, lang => jl}
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

@RunWith(value = classOf[Parameterized])
class ExamplesTest(cached: Boolean) extends AbstractSCROLLTest(cached) {

  @Before
  def initialize(): Unit = {
    // do not want info or debug logging at all here
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error")
  }

  @After
  def shudown(): Unit = {
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info")
  }

  @Test
  def testUniversityExample(): Unit = {
    UniversityExample.main(null)
  }

  @Test
  def testBankExample(): Unit = {
    BankExample.main(null)
  }

  @Test
  def testAPICallsExample(): Unit = {
    APICallsExample.main(null)
  }

  @Test
  def testRobotExample(): Unit = {
    RobotExample.main(null)
  }

  @Test
  def testExpressionProblemExample(): Unit = {
    ExpressionProblemExample.main(null)
  }

}

object ExamplesTest {
    @Parameters
    def parameters: ju.Collection[Array[jl.Boolean]] = {
        val list = new ju.ArrayList[Array[jl.Boolean]]()
        list.add(Array(true))
        list.add(Array(false))
        list
    }
}
