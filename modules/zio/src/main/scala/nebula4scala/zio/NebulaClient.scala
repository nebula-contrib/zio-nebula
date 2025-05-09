package nebula4scala.zio

import com.vesoft.nebula.client.graph.NebulaPoolConfig as _
import com.vesoft.nebula.client.graph.net.NebulaPool as Pool

import _root_.zio._
import nebula4scala.SyncFuture
import nebula4scala.api.{ NebulaClient, NebulaSession }
import nebula4scala.data._
import nebula4scala.impl.NebulaClientDefault

object NebulaClient {

  private final class Impl(underlying: NebulaClient[SyncFuture]) extends NebulaClient[Task] {

    def init(poolConfig: NebulaPoolConfig): Task[Boolean] =
      ZIO.fromFuture(ec => underlying.init(poolConfig))

    def close(): Task[Unit] = ZIO.attempt(underlying.close())

    def openSession(poolConfig: NebulaPoolConfig): Task[NebulaSession[Task]] =
      ZIO.fromFuture(implicit ec => underlying.openSession(poolConfig).map(s => new NebulaSessionImpl(s)))

    def openSession(poolConfig: NebulaPoolConfig, useSpace: Boolean): Task[NebulaSession[Task]] =
      ZIO.fromFuture(implicit ec => underlying.openSession(poolConfig, useSpace).map(s => new NebulaSessionImpl(s)))

    def activeConnNum: Task[Int] = ZIO.fromFuture(ec => underlying.activeConnNum)

    def idleConnNum: Task[Int] = ZIO.fromFuture(ec => underlying.idleConnNum)

    def waitersNum: Task[Int] = ZIO.fromFuture(ec => underlying.waitersNum)

  }

  private def makePool: ZIO[Scope, Nothing, Pool] = ZIO.acquireRelease(ZIO.succeed(new Pool))(d =>
    ZIO.attempt(d.close()).onError(e => ZIO.logErrorCause(e)).ignoreLogged
  )

  lazy val layer: ZLayer[Scope, Throwable, NebulaClient[Task]] =
    ZLayer.fromZIO(makePool.map(pool => new Impl(new NebulaClientDefault(pool))))
}
