package nebula4scala.impl

import scala.concurrent._
import scala.jdk.CollectionConverters._
import scala.util.Try

import com.vesoft.nebula.client.graph.net.Session

import nebula4scala.api.NebulaSession
import nebula4scala.data.NebulaHostAddress
import nebula4scala.data.input._
import nebula4scala.syntax._

final class NebulaSessionDefault(private val underlying: Session) extends NebulaSession[Try] {

  def execute(stmt: Stmt): Try[stmt.T] =
    Try(blocking {
      stmt match {
        case StringStmt(_stmt) =>
          new NebulaResultSetDefault(underlying.execute(_stmt)).asInstanceOf[stmt.T]
        case StringStmtWithMap(_stmt, parameterMap) =>
          new NebulaResultSetDefault(underlying.executeWithParameter(_stmt, parameterMap.asJava)).asInstanceOf[stmt.T]
        case JsonStmt(jsonStmt) =>
          underlying
            .executeJson(jsonStmt)
            .asInstanceOf[stmt.T]
        case JsonStmtWithMap(jsonStmt, parameterMap) =>
          underlying
            .executeJsonWithParameter(jsonStmt, parameterMap.asJava)
            .asInstanceOf[stmt.T]
      }
    })

  def ping(): Try[Boolean] = Try(blocking(underlying.ping()))

  def pingSession(): Try[Boolean] = Try(blocking(underlying.pingSession()))

  def release(): Try[Unit] = Try(blocking(underlying.release()))

  def graphHost: Try[NebulaHostAddress] = Try {
    blocking {
      val addr = underlying.getGraphHost
      NebulaHostAddress(addr.getHost, addr.getPort)
    }
  }

  def sessionID: Try[Long] = Try(blocking(underlying.getSessionID))

  def close(): Try[Unit] = Try(blocking(underlying.close()))

}
