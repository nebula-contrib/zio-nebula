package zio.nebula.net

import scala.jdk.CollectionConverters._

import zio._
import zio.nebula.NebulaResultSet

import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.graph.net.Session

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/29
 */
final class NebulaSession(private val underlying: Session) {

  def execute(stmt: String): Task[NebulaResultSet] = ZIO.attempt(new NebulaResultSet(underlying.execute(stmt)))

  def executeWithParameter(stmt: String, parameterMap: Map[String, AnyRef]): Task[NebulaResultSet] =
    ZIO.attempt(new NebulaResultSet(underlying.executeWithParameter(stmt, parameterMap.asJava)))

  def executeJson(stmt: String): Task[String] =
    ZIO.attempt(underlying.executeJson(stmt))

  def executeJsonWithParameter(stmt: String, parameterMap: Map[String, AnyRef]): Task[String] =
    ZIO.attempt(underlying.executeJsonWithParameter(stmt, parameterMap.asJava))

  def ping: Task[Boolean] = ZIO.attempt(underlying.ping())

  def pingSession: Task[Boolean] = ZIO.attempt(underlying.pingSession())

  def release(): Task[Unit] = ZIO.attempt(underlying.release())

  def getGraphHost: Task[HostAddress] = ZIO.attempt(underlying.getGraphHost)

  def getSessionID: Task[Long] = ZIO.attempt(underlying.getSessionID)

  def close(): Task[Unit] = ZIO.attempt(underlying.close())

}
