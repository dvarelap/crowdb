package crowdb

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class CriteriaSpec extends Specification with Mockito {

  "Single statements" should {
    "build a valid Eq criterion"        in { EqCriterion("name", 1).sql     === "name = ?" }
    "build a valid NotEq criterion"     in { NotEqCriterion("name", 1).sql  === "name != ?" }
    "build a valid In criterion"        in { InCriterion("name", 1).sql     === "name IN (?)" }
    "build a valid NotIn criterion"     in { NotInCriterion("name", 1).sql  === "name NOT IN (?)" }
    "build a valid IsNull criterion"    in { IsNullCriterion("name").sql    === "name IS NULL" }
    "build a valid IsNotNull criterion" in { IsNotNullCriterion("name").sql === "name IS NOT NULL" }

    "build a valid > criterion"  in { GreaterThanCriterion("name", 1).sql   === "name > ?" }
    "build a valid >= criterion" in { GreaterEqThanCriterion("name", 1).sql === "name >= ?" }
    "build a valid < criterion"  in { LessThanCriterion("name", 1).sql      === "name < ?" }
    "build a valid <= criterion" in { LessEqThanCriterion("name", 1).sql    === "name <= ?" }
  }

  "Composed statements" should {
    val eq  = EqCriterion("name", 1)
    val neq = NotEqCriterion("alst", 1)
    val in = InCriterion("age", 1)

    "build a valid AND Statement criterion" in {
      AndCriterion(Seq(eq, neq)).sql      === "(name = ? AND alst != ?)"
      AndCriterion(Seq(eq, in)).sql       === "(name = ? AND age IN (?))"
      AndCriterion(Seq(eq, in, neq)).sql  === "(name = ? AND age IN (?) AND alst != ?)"
    }

    "build a valid OR Statement criterion" in {
      OrCriterion(Seq(eq, neq)).sql      === "(name = ? OR alst != ?)"
      OrCriterion(Seq(eq, in)).sql       === "(name = ? OR age IN (?))"
      OrCriterion(Seq(eq, in, neq)).sql  === "(name = ? OR age IN (?) OR alst != ?)"
    }

    "build a valid Componsed statement criterion" in {
      val and1 = AndCriterion(Seq(eq, neq))
      val and2 = AndCriterion(Seq(eq, in))
      OrCriterion(Seq(and1, in)).sql === "((name = ? AND alst != ?) OR age IN (?))"
      OrCriterion(Seq(and1, and2)).sql === "((name = ? AND alst != ?) OR (name = ? AND age IN (?)))"
    }
  }
}
