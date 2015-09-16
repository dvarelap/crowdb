package crowdb

trait Model {
  private[crowdb] var _id: Long = 0

  def id: Long = _id
  def isNew: Boolean = id == 0
}
