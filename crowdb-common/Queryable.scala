package crowdb

trait Queryable {
  def sql: String
  def values: Seq[Any]
}
