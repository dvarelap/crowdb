package crowdb
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class StatementsSpec extends Specification with Mockito {

  val descriptor = TableDescriptor("user", "id", "first_name", "last_name")

  "SELECT statement" should {
    "create a correct sql text" in {
      val stmt    = SelectStatement(descriptor)
      val select  = stmt.where("first_name = ?", Seq("fido"))
      select.sql          === """SELECT first_name, last_name, id FROM "user" WHERE first_name = ?"""

      select.values.size  === 1
    }

    "create a correct with criteria dsl" in {
      import dsl._
      val stmt    = SelectStatement(descriptor)
      val query   = "first_name" :== "fido"
      val select  = stmt.where(query.sql, query.values)
      select.sql          === """SELECT first_name, last_name, id FROM "user" WHERE first_name = ?"""

      select.values.size  === 1
      select.values(0)    === "fido"
    }

    "create a correct with criteria dsl when composed" in {
      import dsl._
      val stmt    = SelectStatement(descriptor)
      val query   = ("first_name" :== "fido") :&& ("id" :!== 2)
      val select  = stmt.where(query.sql, query.values)
      select.sql          === """SELECT first_name, last_name, id FROM "user" WHERE first_name = ? AND id != ?"""

      select.values.size  === 2
      select.values(0)    === "fido"
      select.values(1)    === 2
    }

    "create a correct with criteria dsl when composed > 3 elements" in {
      import dsl._
      val stmt    = SelectStatement(descriptor)
      val query   = ("first_name" :== "fido") :&& ("id" :> 2) :&& ("last_name" :!== "pulgas")
      val select  = stmt.where(query.sql, query.values)
      select.sql          === """SELECT first_name, last_name, id FROM "user" WHERE (first_name = ? AND id > ?) AND last_name != ?"""

      select.values.size  === 3
      select.values(0)    === "fido"
      select.values(1)    === 2
      select.values(2)    === "pulgas"
    }

    "create a correct with criteria dsl when composed > 3 elements in different priority order" in {
      import dsl._
      val stmt    = SelectStatement(descriptor)
      val query   = ("first_name" :== "fido") :&& (("id" :> 2) :&& ("last_name" :!== "pulgas"))
      val select  = stmt.where(query.sql, query.values)
      select.sql          === """SELECT first_name, last_name, id FROM "user" WHERE first_name = ? AND (id > ? AND last_name != ?)"""

      select.values.size  === 3
      select.values(0)    === "fido"
      select.values(1)    === 2
      select.values(2)    === "pulgas"
    }
  }

  "FIND statement" should {
    "create a correct sql text" in {
      val stmt    = FindStatement(descriptor)
      val select  = stmt whereId 1
      select.sql          === """SELECT first_name, last_name, id FROM "user" WHERE id = ? LIMIT 1"""
      select.values.size  === 1
    }
  }

  "UPDATE statement" should {
    "create a correct sql text" in {
      val stmt    = UpdateStatement(descriptor)
      val update  = stmt.values(Seq("Jorge", "Wax")).whereId(1)

      update.sql          === """UPDATE "user" SET first_name = ?, last_name = ? WHERE id = ?"""
      update.values.size  === 3
    }
  }

  "INSERT statement" should {
    "create a correct sql text" in {
      val stmt    = InsertStatement(descriptor)
      val update  = stmt.values(Seq("Jorge", "Wax"))

      update.sql          === """INSERT INTO "user" (first_name, last_name) VALUES (?, ?) RETURNING id"""
      update.values.size  === 2
    }
  }

  "DELETE statement" should {
    "create a correct sql text" in {
      val stmt    = DeleteStatement(descriptor)
      val update  = stmt whereId 1

      update.sql          === """DELETE FROM "user" WHERE id = ?"""
      update.values.size  === 1
    }
  }
}
