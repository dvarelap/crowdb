import crowdb._
import crowdb.macros._

case class User(firstName: String, aaaa: Option[String]) extends Model

object Db extends DbInstance(DbConfig()) {
  import DbMacros._

  val users = table[User]

}

object Tses extends App {
  import Db._

  inTransaction { implicit c =>
    users.create(User("da", None))
  }
}
