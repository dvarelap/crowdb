package io.crowdb.macros

import io.crowdb._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import com.github.mauricio.async.db.RowData



class Impl(val c: Context) {

  def getTableName[T: c.WeakTypeTag](): String = {
    import c.universe._

    val t = weakTypeOf[T]

    extractNameFromAnnotation(t.typeSymbol.annotations.toString, "Table") match {
      case Some(a)  => a
      case None     => snakify(t.toString.reverse.takeWhile(_ != '.').reverse)
    }
  }

  private def extractNameFromAnnotation(annotationListAsString: String, annotationName: String): Option[String] = {
    val i = annotationListAsString.indexOf(annotationName + "(\"")
    if (i >= 0)
      Some(annotationListAsString.drop(i + annotationName.length + 2).takeWhile(_ != '"'))
    else
      None
  }

  def buildValToColumnMap[T: c.WeakTypeTag](): Map[String, String] = {
    import c.universe._

    val t     = weakTypeOf[T]
    val vals  = t.members.filter(_.asTerm.isVal)
    vals.map(c => {
      val valName     = c.name.toString.trim
      val columnName  = extractNameFromAnnotation(c.annotations.toString, "Column @scala.annotation.meta.field") match {
        case Some(a)  => a
        case None     => snakify(valName)
      }
      (valName, columnName)
    }).toMap
  }

  def rowToModel_impl[T: c.WeakTypeTag](row: c.Expr[RowData]): c.Expr[T] = {
    import c.universe._

    val t           = weakTypeOf[T]
    val vals        = t.members.filter(_.asTerm.isVal)
    val valNames    = vals.map(_.name.toString.trim)
    val valToColumn = buildValToColumnMap[T]()

    val assignments = vals.map(c => {
      val valName     = c.name.toString.trim
      val columnName  = valToColumn(valName)

      if (c.typeSignature.typeSymbol.name.toString.trim == "Option")
        q"`${TermName(valName)}` = Option($row.apply($columnName).asInstanceOf[${c.typeSignature.typeArgs.head}])"
      else
        q"`${TermName(valName)}` = $row.apply($columnName).asInstanceOf[${c.typeSignature.resultType}]"
    })
    val q = q"""new `$t`(..$assignments)"""
    c.Expr(q)
  }

  def table_impl[T: c.WeakTypeTag, U]() = {
    import c.universe._

    val id          = "id"
    val t           = weakTypeOf[T]
    val tableName   = getTableName[T]()
    val valToColumn = buildValToColumnMap[T]()
    val vals        = t.members.toSeq.filter(_.asTerm.isVal).reverse
    val valNames    = vals.map(v => snakify(v.name.toString.trim))

    val assignments = vals.map { c =>
      val valName     = c.name.toString.trim
      val columnName  = valToColumn(valName)

      if (c.typeSignature.typeSymbol.name.toString.trim == "Option")
        q"`${TermName(valName)}` = Option(row.apply($columnName).asInstanceOf[${c.typeSignature.typeArgs.head}])"
      else
        q"`${TermName(valName)}` = row.apply($columnName).asInstanceOf[${c.typeSignature.resultType}]"
    }

    val toVals = vals.map { c =>
      val valName = c.name.toString.trim
      q"m.${TermName(valName)}"
    }
    // /*{ override val id = row.apply($id).asInstanceOf[Long]} */
    q"new Table[`$t`](new TableDescriptor($tableName, $id, ..$valNames), (row) => new `$t`(..$assignments), (m) => Seq(..$toVals))"
  }


  protected def snakify(name: String): String = {
    name.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2").replaceAll("([a-z\\d])([A-Z])", "$1_$2").toLowerCase
  }

}

object DbMacros {
  def rowToClass[T](row: RowData): T = macro Impl.rowToModel_impl[T]
  def table[T](): Table[T]           = macro Impl.table_impl[T, String]
}
