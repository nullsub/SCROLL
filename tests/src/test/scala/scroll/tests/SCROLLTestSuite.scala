package scroll.tests

import org.scalatest.Suites

object SCROLLTestSuite {
  val suites: Seq[AbstractSCROLLTest] = Seq(Seq(false, true)).flatMap(args => {
    //val suites: Seq[AbstractSCROLLTest] = Seq(Seq(true, false), Seq(true, true), Seq(false, false), Seq(false, true)).flatMap(args => {
    val c = args(0)
    val j = args(1)
    Seq(
      //new RoleFeaturesTest(cached = c, jastAdd = j),
      new RoleSortingTest(cached = c, jastAdd = j),
      new DynamicExtensionsTest(cached = c, jastAdd = j),
      //new EqualityRoleTest(cached = c, jastAdd = j))
     // new ExamplesTest(cached = c, jastAdd = j),
      new RelationshipTest(cached = c, jastAdd = j))
     /*  new UnionTypesTest(cached = c, jastAdd = j),
       new FormalCROMTest(cached = c, jastAdd = j),
       new FormalCROMExampleTest(cached = c, jastAdd = j),
       new ECoreInstanceTest(cached = c, jastAdd = j),
       new CROITest(cached = c, jastAdd = j),
       new RoleConstraintsTest(cached = c, jastAdd = j),
       new RolePlayingAutomatonTest(cached = c, jastAdd = j),
       new RoleRestrictionsTest(cached = c, jastAdd = j),
        new RoleGroupsTest(cached = c, jastAdd = j),
       new MultiRoleFeaturesTest(cached = c, jastAdd = j),
      // new FacetsTest(cached = c, jastAdd = j),
      new RecursiveBaseCallsWithClassesTest(cached = c, jastAdd = j),
     new RecursiveBaseCallsWithCaseClassesTest(cached = c, jastAdd = j),
      new ThrowableInRoleMethodsTest(cached = c, jastAdd = j))*/
  })
}

class SCROLLTestSuite extends Suites(SCROLLTestSuite.suites: _*)