package crowdb

case class TableDescriptor(tableName: String, identityName: String, columns: String*) {
  val queryCols = columns :+ identityName
}
