package crowdb
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class StatementsSpec extends Specification with Mockito {

  val descriptor = TableDescriptor("user", "id", "first_name", "last_name")

  "SELECT statement" should {
    "create a correct sql text" in {
      val stmt    = SelectStatement(descriptor)
      val select  = stmt.where("first_name = ?", "fido")
      select.sql          === "SELECT first_name, last_name, id FROM user WHERE first_name = ?"
      select.values.size  === 1
    }
  }

  "FIND statement" should {
    "create a correct sql text" in {
      val stmt    = FindStatement(descriptor)
      val select  = stmt whereId 1
      select.sql          === "SELECT first_name, last_name, id FROM user WHERE id = ? LIMIT 1"
      select.values.size  === 1
    }
  }

  "UPDATE statement" should {
    "create a correct sql text" in {
      val stmt    = UpdateStatement(descriptor)
      val update  = stmt.values("Jorge", "Wax").whereId(1)

      update.sql          === "UPDATE user SET first_name = ?, last_name = ? WHERE id = ?"
      update.values.size  === 3
    }
  }

  "INSERT statement" should {
    "create a correct sql text" in {
      val stmt    = InsertStatement(descriptor)
      val update  = stmt.values("Jorge", "Wax")

      update.sql          === "INSERT INTO user (first_name, last_name) VALUES (?, ?) RETURNING id"
      update.values.size  === 2
    }
  }

  "DELETE statement" should {
    "create a correct sql text" in {
      val stmt    = DeleteStatement(descriptor)
      val update  = stmt whereId 1

      update.sql          === "DELETE FROM user WHERE id = ?"
      update.values.size  === 1
    }
  }
}
