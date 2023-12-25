package zio.nebula.net

import scala.jdk.CollectionConverters._

import zio._
import zio.nebula.{ GlobalSettings, NebulaResultSet }

import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.graph.net.Session

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/29
 */
final class NebulaSession(private val underlying: Session) {

  def execute(stmt: Stmt): Task[stmt.T] =
    GlobalSettings.printLog(stmt.toString) *> ZIO.attempt {
      stmt match {
        case StringStmt(_stmt)                       =>
          new NebulaResultSet(underlying.execute(_stmt)).asInstanceOf[stmt.T]
        case StringStmtWithMap(_stmt, parameterMap)  =>
          new NebulaResultSet(underlying.executeWithParameter(_stmt, parameterMap.asJava)).asInstanceOf[stmt.T]
        case JsonStmt(jsonStmt)                      =>
          underlying
            .executeJson(jsonStmt)
            .asInstanceOf[stmt.T]
        case JsonStmtWithMap(jsonStmt, parameterMap) =>
          underlying
            .executeJsonWithParameter(jsonStmt, parameterMap.asJava)
            .asInstanceOf[stmt.T]
      }
    }

  def ping(): Task[Boolean] = ZIO.attempt(underlying.ping())

  def pingSession(): Task[Boolean] = ZIO.attempt(underlying.pingSession())

  def release(): Task[Unit] = ZIO.attempt(underlying.release())

  def graphHost: Task[HostAddress] = ZIO.attempt(underlying.getGraphHost)

  def sessionID: Task[Long] = ZIO.attempt(underlying.getSessionID)

  def close(): Task[Unit] = ZIO.attempt(underlying.close())

}
