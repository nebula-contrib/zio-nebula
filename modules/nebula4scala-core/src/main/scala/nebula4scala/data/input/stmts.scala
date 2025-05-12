package nebula4scala.data.input

import scala.annotation.implicitNotFound

import nebula4scala.api.NebulaResultSet
import nebula4scala.data.input.Stmt._

@implicitNotFound(msg =
  "No given instance of type Context[${F}] was found. Please provide an implicit instance of Context[${F}] to use the str method."
)
trait Context[F[_]]

sealed trait Stmt {
  type T
}

object Stmt {

  sealed trait BaseStmt[F[_]] extends Stmt {
    def stmt: String

    override type T = NebulaResultSet[F]
  }

  def str[F[_]: Context](stmt: String): StringStmt[F] = StringStmt[F](stmt)

  def strMap[F[_]: Context](stmt: String, parameterMap: Map[String, AnyRef]): StringStmtWithMap[F] =
    StringStmtWithMap[F](stmt, parameterMap)

  def json(stmt: String): JsonStmt = JsonStmt(stmt)

  def jsonMap(stmt: String, parameterMap: Map[String, AnyRef]): JsonStmtWithMap = JsonStmtWithMap(stmt, parameterMap)
}

final case class StringStmt[F[_]: Context](stmt: String) extends BaseStmt[F]

final case class StringStmtWithMap[F[_]: Context](stmt: String, parameterMap: Map[String, AnyRef]) extends BaseStmt[F]

final case class JsonStmt(jsonStmt: String) extends Stmt {
  override type T = String
}

final case class JsonStmtWithMap(jsonStmt: String, parameterMap: Map[String, AnyRef]) extends Stmt {
  override type T = String
}
