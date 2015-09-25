package crowdb

trait Statement extends Queryable

case class CrudStatements(
  select: SelectStatement,
  find:   FindStatement,
  insert: InsertStatement,
  update: UpdateStatement,
  delete: DeleteStatement
)

case class SelectStatement(
  descriptor: TableDescriptor,
  where    : Option[String] = None,
  values   : Seq[Any] = Seq()) extends Statement {

  private val cols    = descriptor.queryCols.mkString(", ")
  private val whereSt = where.getOrElse("")
  val sql     = s"""SELECT $cols FROM "${descriptor.tableName}" $whereSt"""

  def where(where: String, values: Seq[Any]) = {
    copy(where = Some(s"WHERE $where"), values = values)
  }
}

case class FindStatement(
  descriptor: TableDescriptor,
  id       : Option[Long] = None) extends Statement {

  private val cols    = descriptor.queryCols.mkString(", ")

  val values = id match {
    case Some(i) => Seq(i)
    case None    => Seq()
  }

  val sql = s"""SELECT $cols FROM "${descriptor.tableName}" WHERE id = ? LIMIT 1"""

  def whereId(id: Long) = copy(id = Some(id))
}


case class InsertStatement(
  descriptor: TableDescriptor,
  values   : Seq[Any] = Seq()) extends Statement {

  val wildcards   = Seq.fill(descriptor.columns.size)("?").mkString(", ")
  val columnsStr  = descriptor.columns.mkString(", ")

  val sql = s"""INSERT INTO "${descriptor.tableName}" ($columnsStr) VALUES ($wildcards) RETURNING id"""

  def values(values: Seq[Any]) = copy(values = values)

}

case class UpdateStatement(
  descriptor: TableDescriptor,
  vals     : Seq[Any] = Seq(),
  id       : Option[Long] = None) extends Statement {

  private[this] val wildcards = descriptor.columns.map(column => s"$column = ?").mkString(", ")

  val sql     = s"""UPDATE "${descriptor.tableName}" SET $wildcards WHERE id = ?"""
  val values  = id match {
    case None       => vals
    case Some(key)  => vals :+ key
  }

  def values(values: Seq[Any]) = copy(vals = values)
  def whereId(id: Long)        = copy(id = Option(id))
}


case class DeleteStatement(
  descriptor: TableDescriptor,
  id       : Option[Long] = None) extends Statement {

  val sql     = s"""DELETE FROM "${descriptor.tableName}" WHERE id = ?"""
  val values  = id match {
    case None       => Seq()
    case Some(key)  => Seq(key)
  }

  def whereId(id: Long)        = copy(id = Option(id))
}

// transactional statements
case class BeginStatement(sql:String = "BEGIN;", values: Seq[Any] = Seq()) extends Statement
case class CommitStatement(sql:String = "COMMIT;", values: Seq[Any] = Seq()) extends Statement
case class RollbackStatement(sql:String = "ROLLBACK;", values: Seq[Any] = Seq()) extends Statement
