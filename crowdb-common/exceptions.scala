package crowdb

sealed abstract class CrowdbException(message: String, cause: Option[Throwable] = None) extends Exception(message, cause.orNull)
case class DatabaseException(message: String, cause: Option[Throwable] = None) extends CrowdbException(message, cause)
