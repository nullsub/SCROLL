package scroll.tests

import scroll.tests.mocks.SomeCompartment

abstract class AbstractSCROLLTest(cached: Boolean) {

  class CompartmentUnderTest() extends SomeCompartment(cached)

}
