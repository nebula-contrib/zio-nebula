package nebula4scala.data.input

import nebula4scala.data.*

sealed trait Stmt {
  type T
}

object Stmt {
  def str(stmt: String): StringStmt = StringStmt(stmt)

  def strMap(stmt: String, parameterMap: Map[String, AnyRef]): StringStmtWithMap = StringStmtWithMap(stmt, parameterMap)

  def json(stmt: String): JsonStmt = JsonStmt(stmt)

  def jsonMap(stmt: String, parameterMap: Map[String, AnyRef]): JsonStmtWithMap = JsonStmtWithMap(stmt, parameterMap)
}

final case class StringStmt(stmt: String) extends Stmt {
  override type T = NebulaResultSet
}

final case class StringStmtWithMap(stmt: String, parameterMap: Map[String, AnyRef]) extends Stmt {
  override type T = NebulaResultSet

}

final case class JsonStmt(jsonStmt: String) extends Stmt {
  override type T = String

}

final case class JsonStmtWithMap(jsonStmt: String, parameterMap: Map[String, AnyRef]) extends Stmt {
  override type T = String

}
