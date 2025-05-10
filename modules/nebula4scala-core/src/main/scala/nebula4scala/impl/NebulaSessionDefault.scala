package nebula4scala.impl

import scala.concurrent.{ blocking, Future }
import scala.jdk.CollectionConverters._
import scala.util._

import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.graph.net.Session

import nebula4scala.api.NebulaSession
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.syntax._

final class NebulaSessionDefault(private val underlying: Session) extends NebulaSession[SyncFuture] {

  def execute(stmt: Stmt): SyncFuture[stmt.T] =
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

  def ping(): SyncFuture[Boolean] = Future(blocking(underlying.ping()))

  def pingSession(): SyncFuture[Boolean] = Future(blocking(underlying.pingSession()))

  def release(): SyncFuture[Unit] = Future(blocking(underlying.release()))

  def graphHost: SyncFuture[HostAddress] = Future(blocking(underlying.getGraphHost))

  def sessionID: SyncFuture[Long] = Future(blocking(underlying.getSessionID))

  def close(): SyncFuture[Unit] = Future(blocking(underlying.close()))

}
