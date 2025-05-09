package nebula4scala.zio

import zio.*

import com.vesoft.nebula.client.graph.data.HostAddress

import nebula4scala.SyncFuture
import nebula4scala.api.NebulaSession
import nebula4scala.data.*
import nebula4scala.data.input.*

final class NebulaSessionImpl(private val underlying: NebulaSession[SyncFuture]) extends NebulaSession[Task] {

  def execute(stmt: Stmt): Task[stmt.T] =
    ZIO.fromFuture(ec => underlying.execute(stmt))

  def ping(): Task[Boolean] = ZIO.fromFuture(ec => underlying.ping())

  def pingSession(): Task[Boolean] = ZIO.fromFuture(ec => underlying.pingSession())

  def release(): Task[Unit] = ZIO.fromFuture(ec => underlying.release())

  def graphHost: Task[HostAddress] = ZIO.fromFuture(ec => underlying.graphHost)

  def sessionID: Task[Long] = ZIO.fromFuture(ec => underlying.sessionID)

  def close(): Task[Unit] = ZIO.fromFuture(ec => underlying.close())

}
