package nebula4scala.impl

import scala.concurrent._
import scala.jdk.CollectionConverters._

import com.vesoft.nebula.client.graph.net.Session

import nebula4scala.api.NebulaSession
import nebula4scala.data.NebulaHostAddress
import nebula4scala.data.input._
import nebula4scala.syntax._

final class NebulaSessionDefault(private val underlying: Session) extends NebulaSession[ScalaFuture] {

  def execute(stmt: Stmt): ScalaFuture[stmt.T] =
    Future(blocking {
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

  def ping(): ScalaFuture[Boolean] = Future(blocking(underlying.ping()))

  def pingSession(): ScalaFuture[Boolean] = Future(blocking(underlying.pingSession()))

  def release(): ScalaFuture[Unit] = Future(blocking(underlying.release()))

  def graphHost: ScalaFuture[NebulaHostAddress] = Future {
    blocking {
      val addr = underlying.getGraphHost
      NebulaHostAddress(addr.getHost, addr.getPort)
    }
  }

  def sessionID: ScalaFuture[Long] = Future(blocking(underlying.getSessionID))

  def close(): ScalaFuture[Unit] = Future(blocking(underlying.close()))

}
