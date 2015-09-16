package crowdb

trait Criterion extends Queryable

abstract class SingleCriterion[T](val sql: String, maybeTo: Option[T] = None) extends Criterion {
  val values  = maybeTo match {
    case None     => Seq()
    case Some(to) => Seq(to)
  }
}
abstract class ComposedCriterion(sep: String, criteria: Seq[Criterion]) extends Criterion {
  val values = criteria.flatMap(_.values)
  val sql = criteria.map(_.sql).mkString("(", s" $sep ", ")")
}

case class EqCriterion[T](column: String,  to: T)
  extends SingleCriterion[T](s"$column = ?", Option(to))

case class NotEqCriterion[T](column: String,  to: T)
  extends SingleCriterion[T](s"$column != ?", Option(to))

case class GreaterThanCriterion[T](column: String,  to: T)
  extends SingleCriterion[T](s"$column > ?", Option(to))

case class GreaterEqThanCriterion[T](column: String,  to: T)
  extends SingleCriterion[T](s"$column >= ?", Option(to))

case class LessThanCriterion[T](column: String,  to: T)
  extends SingleCriterion[T](s"$column < ?", Option(to))

case class LessEqThanCriterion[T](column: String,  to: T)
  extends SingleCriterion[T](s"$column <= ?", Option(to))

case class InCriterion[T](column: String,  to: T)
  extends SingleCriterion[T](s"$column IN (?)", Option(to))

case class NotInCriterion[T](column: String,  to: T)
  extends SingleCriterion[T](s"$column NOT IN (?)", Option(to))

case class IsNullCriterion[T](column: String)
  extends SingleCriterion[T](s"$column IS NULL")

case class IsNotNullCriterion[T](column: String)
  extends SingleCriterion[T](s"$column IS NOT NULL")

// composed
case class AndCriterion(criteria: Seq[Criterion])
  extends ComposedCriterion("AND", criteria)

case class OrCriterion(criteria: Seq[Criterion])
  extends ComposedCriterion("OR", criteria)


object dsl {
  implicit def toOp[T](colName: String): Operator[T] = Operator[T](colName)
}


case class Operator[T](colName: String) {
  def :==(value2: T)            = EqCriterion(colName, value2)
  def :!==(value2: T)           = NotEqCriterion(colName, value2)
  def :>(value2: T)             = GreaterThanCriterion(colName, value2)
  def :>=(value2: T)            = GreaterEqThanCriterion(colName, value2)
  def :<(value2: T)             = LessThanCriterion(colName, value2)
  def :<=(value2: T)            = LessEqThanCriterion(colName, value2)
  def in(value2: T)             = InCriterion(colName, value2)
  def notIn(value2: T)          = NotInCriterion(colName, value2)
  def isNull                    = IsNullCriterion(colName)
  def isNotNull                 = IsNotNullCriterion(colName)
  // def :&&(criterion: Criterion)
  // :||
}
