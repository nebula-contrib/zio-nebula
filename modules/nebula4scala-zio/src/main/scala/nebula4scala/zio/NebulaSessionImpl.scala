package nebula4scala.zio

import zio._

import com.vesoft.nebula.client.graph.data.HostAddress

import nebula4scala.api._
import nebula4scala.data.input._
import nebula4scala.syntax._

final class NebulaSessionImpl(private val underlying: NebulaSession[ScalaFuture]) extends NebulaSession[Task] {

  def execute(stmt: Stmt): Task[stmt.T] = {
    ZIO
      .blocking(ZIO.fromFuture(_ => underlying.execute(stmt)))
      .map {
        case set: NebulaResultSet[_] =>
          new NebulaResultSetImpl(set.asInstanceOf[NebulaResultSet[ScalaFuture]])
        case str: String => str
      }
      .map(_.asInstanceOf[stmt.T])
  }

  def ping(): Task[Boolean] = ZIO.blocking(ZIO.fromFuture(_ => underlying.ping()))

  def pingSession(): Task[Boolean] = ZIO.blocking(ZIO.fromFuture(_ => underlying.pingSession()))

  def release(): Task[Unit] = ZIO.blocking(ZIO.fromFuture(_ => underlying.release()))

  def graphHost: Task[HostAddress] = ZIO.blocking(ZIO.fromFuture(_ => underlying.graphHost))

  def sessionID: Task[Long] = ZIO.blocking(ZIO.fromFuture(_ => underlying.sessionID))

  def close(): Task[Unit] = ZIO.blocking(ZIO.fromFuture(_ => underlying.close()))

}
