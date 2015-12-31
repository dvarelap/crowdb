package io.crowdb
import scala.annotation._
class TableName(val name: String, val idName: String = "id") extends Annotation with StaticAnnotation
class Column(val name: String) extends Annotation with StaticAnnotation
