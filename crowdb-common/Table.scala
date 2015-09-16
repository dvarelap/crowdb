package crowdb

case class Table[M <: Model](descriptor: TableDescriptor) {

  private[this] val select = SelectStatement(descriptor)
  private[this] val find   = FindStatement(descriptor)
  private[this] val delete = DeleteStatement(descriptor)
  private[this] val update = UpdateStatement(descriptor)
  private[this] val insert = InsertStatement(descriptor)

  def create(m: M): M = {
    println(select.sql)
    println(find.sql)
    println(delete.sql)
    println(update.sql)
    println(insert.sql)

    m._id = 1
    m
  }
  def update(m: M): M = ???
  def find(id: Long): Option[M] = ???
  def delete(id: Long): Unit = ???
}
