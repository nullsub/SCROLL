package scroll.tests

import java.{util => ju, lang => jl}
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertArrayEquals

import scroll.tests.mocks.{CoreA, CoreB}

@RunWith(value = classOf[Parameterized])
class FacetsTest(cached: Boolean) extends AbstractSCROLLTest(cached) {

  object TestFacet extends Enumeration {
    type Color = Value
    val Red, Blue, Green = Value
  }

  import TestFacet._

  @Test
  def testAddingFacets(): Unit = {
    val someCore = new CoreA()
    new CompartmentUnderTest() {
      val player = someCore <+> Red
      assertTrue(player.hasFacets(Red))
    }
  }


  @Test
  def testRemovingFacets(): Unit = {
    val someCore = new CoreA()
    new CompartmentUnderTest() {
      val player = someCore <+> Red
      player.drop(Red)
      assertFalse(player.hasFacets(Red))
    }
  }

  @Test
  def testTransferringFacets(): Unit = {
    val someCoreA = new CoreA()
    val someCoreB = new CoreB()
    new CompartmentUnderTest() {
      val playerA = someCoreA <+> Red
      val playerB = +someCoreB
      someCoreA transfer Red to someCoreB
      assertFalse(playerA.hasFacets(Red))
      assertTrue(playerB.hasFacets(Red))
    }
  }

  @Test
  def testFilteringFacets(): Unit = {
    import scala.collection.JavaConverters._

    val someCoreA1 = new CoreA()
    val someCoreA2 = new CoreA()
    val someCoreA3 = new CoreA()
    val someCoreA4 = new CoreA()
    val someCoreA5 = new CoreA()
    val someCoreA6 = new CoreA()

    new CompartmentUnderTest() {
      someCoreA1 <+> Red
      someCoreA2 <+> Red
      someCoreA3 <+> Red
      someCoreA4 <+> Blue
      someCoreA5 <+> Blue
      someCoreA6 <+> Blue

      assertArrayEquals(Seq(someCoreA1, someCoreA2, someCoreA3).asJava.toArray, all { c: CoreA => c.hasFacets(Red) }.asJava.toArray)

      assertArrayEquals(Seq(someCoreA1, someCoreA2, someCoreA3).asJava.toArray, all { c: CoreA => c.hasSomeFacet(Red) }.asJava.toArray)
      assertArrayEquals(Seq(someCoreA4, someCoreA5, someCoreA6).asJava.toArray, all { c: CoreA => c.hasFacets(Blue) }.asJava.toArray)
      assertArrayEquals(Seq(someCoreA4, someCoreA5, someCoreA6).asJava.toArray, all { c: CoreA => c.hasSomeFacet(Blue) }.asJava.toArray)
      assertArrayEquals(Seq(someCoreA1, someCoreA2, someCoreA3, someCoreA4, someCoreA5, someCoreA6).asJava.toArray, all { c: CoreA => c.hasSomeFacet(Red, Blue) }.asJava.toArray)
      assertTrue(all { c: CoreA => c.hasSomeFacet(Green) }.isEmpty)
      assertTrue(all { c: CoreA => c.hasFacets(Green) }.isEmpty)
      assertTrue(all { c: CoreA => c.hasFacets(Red, Blue) }.isEmpty)
      assertTrue(all { c: CoreA => c.hasFacets(Red, Blue, Green) }.isEmpty)
    }
  }

}

object FacetsTest {
    @Parameters
    def parameters: ju.Collection[Array[jl.Boolean]] = {
        val list = new ju.ArrayList[Array[jl.Boolean]]()
        list.add(Array(true))
        list.add(Array(false))
        list
    }
}
