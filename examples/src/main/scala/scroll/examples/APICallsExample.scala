package scroll.examples

import scroll.internal.Compartment
import scroll.internal.support.DispatchQuery.Bypassing
import scroll.internal.support.DispatchQuery.FilterDispatchQuery
import scroll.internal.util.Log.info

object APICallsExample extends App {

  class APICalls extends Compartment {

    case class API() {
      def callA(): Unit = {
        info("Call A is correct.")
      }

      def callB(): Unit = {
        info("Call B is a mess somehow.")
      }

      def callC(): Unit = {
        info("Call C is correct.")
      }
    }

    case class MyApp() {
      val api = API() play FixedAPI()

      def run(): Unit = {
        api.callA()

        api.callB()

        //implicit val dd = Bypassing(_.isInstanceOf[FixedAPI])
        implicit val dd = FilterDispatchQuery(Seq(), Seq(classOf[FixedAPI]), Seq(), Seq())
        val _ = api.callC()
      }
    }

    case class FixedAPI() {
      def callB(): Unit = {
        info("Call B is fixed now. :-)")
      }

      def callC(): Unit = {
        info("Call C is changed too. :-(")
      }
    }

  }

  new APICalls {
    MyApp().run()
  }
}
