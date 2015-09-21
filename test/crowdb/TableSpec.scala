package crowdb
import crowdb.macros._
import com.github.mauricio.async.db._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import com.twitter.util._
import scala.concurrent.{Future => sFuture}
import scala.concurrent.ExecutionContext.Implicits._

class TableSpec extends Specification with Mockito {
  implicit val ex: Executor = new CrowdbExecutor()

  case class User(firstName: String, option: Option[String], age: Int) extends Model

  "#table" should {

    "return a valid converter for the model M" in {
      val rowMock1  = mock[RowData]
      val rowMock2  = mock[RowData]
      val users     = DbMacros.table[User]

      rowMock1.apply("first_name") returns "Fido"
      rowMock1.apply("option")     returns "value"
      rowMock1.apply("age")        returns 1

      rowMock2.apply("first_name") returns "rocky"
      rowMock2.apply("option")     returns null
      rowMock2.apply("age")        returns 2

      users.convert(rowMock1) === User("Fido", Some("value"), 1)
      users.convert(rowMock2) === User("rocky", None, 2)
    }

    "return a valid toVals function for model M" in {
      val users     = DbMacros.table[User]
      users.toVals(User("fido", Some("value"), 1))  === Seq("fido", Some("value"), 1)
      users.toVals(User("rocky", None, 2))          === Seq("rocky", None, 2)

    }
  }

  "#create" should {
    "parse and return a model with the correct id" in {
      val users     = DbMacros.table[User]
      val conn      = mock[Connection]
      val rs        = mock[ResultSet]
      val row       = mock[RowData]
      val qr        = new QueryResult(1, "", Some(rs))

      row.apply("id") returns 1L
      rs.head         returns row

      conn.sendPreparedStatement(any[String], any[Seq[_]]) returns sFuture(qr)

      val createdF = users.create(User("fido", Some("value"), 1))(conn)
      val user     = Await.result(createdF)

      user.isNew  === false
      user.id    !=== 0
    }

    "throw an error if no results are returned" in {
      val users     = DbMacros.table[User]
      val conn      = mock[Connection]
      val rs        = mock[ResultSet]
      val row       = mock[RowData]
      val qr        = new QueryResult(0, "", None)

      row.apply("id") returns 1L
      rs.head         returns row

      conn.sendPreparedStatement(any[String], any[Seq[_]]) returns sFuture(qr)

      val createdF = users.create(User("fido", Some("value"), 1))(conn)
      Await.result(createdF) must throwA[DatabaseException]
    }
  }

  "#update" should {
    "send the query and return the correct model" in {
      val users     = DbMacros.table[User]
      val conn      = mock[Connection]
      val rs        = mock[ResultSet]
      val row       = mock[RowData]
      val qr        = new QueryResult(1, "", Some(rs))

      row.apply("id") returns 1L
      rs.head         returns row

      conn.sendPreparedStatement(any[String], any[Seq[_]]) returns sFuture(qr)

      val createdF = users.create(User("fido", Some("value"), 1))(conn)
      val user     = Await.result(createdF)

      user.isNew  === false
      user.id    !=== 0

      val updatedF = users.update(user)(conn)
      val updated  = Await.result(updatedF)

      updated        === User("fido", Some("value"), 1)
      updated.isNew  === false
      updated.id    !=== 0
    }

    "throw an error if no results are returned" in {
      val users     = DbMacros.table[User]
      val conn      = mock[Connection]
      val rs        = mock[ResultSet]
      val row       = mock[RowData]
      val qr        = new QueryResult(0, "", None)

      rs.head         returns row

      conn.sendPreparedStatement(any[String], any[Seq[_]]) returns sFuture(qr)

      val createdF = users.update(User("fido", Some("value"), 1))(conn)
      Await.result(createdF) must throwA[DatabaseException]
    }
  }

  "#find" should {
    "parse found results to the correct model" in {
      val users     = DbMacros.table[User]
      val conn      = mock[Connection]
      val rs        = mock[ResultSet]
      val row       = mock[RowData]
      val qr        = new QueryResult(1, "", Some(rs))

      row.apply("id")         returns 1L
      row.apply("first_name") returns "Dan"
      row.apply("option")     returns "option"
      row.apply("age")        returns 29
      rs.size                 returns 1
      rs.head                 returns row

      conn.sendPreparedStatement(any[String], any[Seq[_]]) returns sFuture(qr)

      val found     = users.find(1L)(conn)
      val userMaybe = Await.result(found)

      userMaybe   !=== None

      val user = userMaybe.get

      user.isNew      === false
      user.id         === 1
      user.firstName  === "Dan"
      user.option     === Some("option")
      user.age        === 29

    }

    "return None if no results are returned" in {
      val users     = DbMacros.table[User]
      val conn      = mock[Connection]
      val rs        = mock[ResultSet]
      val row       = mock[RowData]
      val qr        = new QueryResult(0, "", Some(rs))

      rs.size         returns 0
      rs.head         returns row

      conn.sendPreparedStatement(any[String], any[Seq[_]]) returns sFuture(qr)

      val found     = users.find(1L)(conn)
      val userMaybe = Await.result(found)

      userMaybe   ==== None
    }
  }
}
