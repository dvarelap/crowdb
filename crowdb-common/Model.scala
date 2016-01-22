package io.crowdb

trait Model {
  def id: Long
  def isNew: Boolean = id == 0
}
