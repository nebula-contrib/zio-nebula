package nebula4scala.impl

import scala.concurrent.Future
import scala.jdk.CollectionConverters._

import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.graph.net.Session

import nebula4scala.api.NebulaSession
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.syntax._

final class NebulaSessionDefault(private val underlying: Session) extends NebulaSession[SyncFuture] {

  def execute(stmt: Stmt): SyncFuture[stmt.T] =
    Future.successful {
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
    }

  def ping(): SyncFuture[Boolean] = Future.successful(underlying.ping())

  def pingSession(): SyncFuture[Boolean] = Future.successful(underlying.pingSession())

  def release(): SyncFuture[Unit] = Future.successful(underlying.release())

  def graphHost: SyncFuture[HostAddress] = Future.successful(underlying.getGraphHost)

  def sessionID: SyncFuture[Long] = Future.successful(underlying.getSessionID)

  def close(): SyncFuture[Unit] = Future.successful(underlying.close())

}
