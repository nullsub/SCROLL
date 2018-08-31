package scroll.tests

import org.scalatest.Suites

object SCROLLTestSuite {
  val suites: Seq[AbstractSCROLLTest] = Seq(true, false).flatMap(c => {
    Seq(
      new RoleFeaturesTest(cached = c),
      new RoleSortingTest(cached = c),
      new DynamicExtensionsTest(cached = c),
      new EqualityRoleTest(cached = c),
      new ExamplesTest(cached = c),
      new RelationshipTest(cached = c),
      new RoleConstraintsTest(cached = c),
      new RoleRestrictionsTest(cached = c),
      new RoleGroupsTest(cached = c),
      new MultiRoleFeaturesTest(cached = c),
      new RecursiveBaseCallsWithClassesTest(cached = c),
      new RecursiveBaseCallsWithCaseClassesTest(cached = c),
      new ThrowableInRoleMethodsTest(cached = c))
  })
}

class SCROLLTestSuite extends Suites(SCROLLTestSuite.suites: _*)
