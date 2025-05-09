package nebula4scala.zio

import scala.jdk.CollectionConverters.*

import zio.*

import com.vesoft.nebula.client.graph.*
import com.vesoft.nebula.client.graph.data.HostAddress

import nebula4scala.SyncFuture
import nebula4scala.api.NebulaSessionClient
import nebula4scala.data.*
import nebula4scala.impl.NebulaSessionClientDefault

object NebulaSessionClient {

  private final class Impl(underlying: NebulaSessionClient[SyncFuture]) extends NebulaSessionClient[Task] {

    override def execute(stmt: String): Task[NebulaResultSet] =
      ZIO.fromFuture(ec => underlying.execute(stmt))

    override def idleSessionNum: Task[Int] = ZIO.fromFuture(ec => underlying.idleSessionNum)

    override def sessionNum: Task[Int] = ZIO.fromFuture(ec => underlying.sessionNum)

    override def isActive: Task[Boolean] = ZIO.fromFuture(ec => underlying.isActive)

    override def isClosed: Task[Boolean] = ZIO.fromFuture(ec => underlying.isClosed)

    override def close(): Task[Unit] = ZIO.fromFuture(ec => underlying.close())

  }

  lazy val layer: ZLayer[Scope & NebulaSessionPoolConfig, Throwable, NebulaSessionClient[Task]] = ZLayer.fromZIO {
    for {
      nebulaConfig <- ZIO.service[NebulaSessionPoolConfig]
      sessionPool <- ZIO.acquireRelease(
        ZIO.attempt(new Impl(NebulaSessionClientDefault.make(nebulaConfig)))
      )(release => release.close().onError(e => ZIO.logErrorCause(e)).ignoreLogged)
    } yield sessionPool
  }
}
