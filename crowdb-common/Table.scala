package crowdb

import com.twitter.util._
import com.github.mauricio.async.db._

case class Table[M <: Model](descriptor: TableDescriptor, convert: RowData => M, toVals: M => Seq[Any])(implicit ex: Executor) {

  private[this] val select = SelectStatement(descriptor)
  private[this] val find   = FindStatement(descriptor)
  private[this] val delete = DeleteStatement(descriptor)
  private[this] val update = UpdateStatement(descriptor)
  private[this] val insert = InsertStatement(descriptor)

  private[this] def identity(m: M, id: Any) = {
    m._id = id.asInstanceOf[Long]
    m
  }

  def create(m: M)(implicit conn: Connection): Future[M] = {
    ex.exec[M](insert values toVals(m), rs => identity(m, rs.head("id")))
  }

  def update(m: M)(implicit conn: Connection): Future[M] = {
    ex.exec[M](update values (toVals(m) :+ m.id), _ => m )
  }

  def find(id: Long)(implicit conn: Connection): Future[Option[M]] = {
    ex.exec[Option[M]](find whereId id, rs => {
      if (rs.size == 1) Option(identity(convert(rs.head), rs.head("id")))
      else None
    })
  }

  def delete(id: Long)(implicit conn: Connection): Future[Unit] = {
    ex.exec[Unit](delete whereId id, _ => Unit )
  }

  def where(criteria: Criterion*): Future[Seq[M]] = ???
}
