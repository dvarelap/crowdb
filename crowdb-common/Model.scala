package crowdb

trait Model {
  private[crowdb] lazy val tableName: String = this.toString
  private[crowdb] var _id: Long = 0

  def id: Long = _id
  def isNew: Boolean = id == 0
}
