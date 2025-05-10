package nebula4scala.zio

import com.vesoft.nebula.client.graph.{ NebulaPoolConfig => _ }
import com.vesoft.nebula.client.graph.net.{ NebulaPool => Pool }

import _root_.zio._
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.impl.NebulaClientDefault
import nebula4scala.syntax._

object NebulaClient {

  private final class Impl(underlying: NebulaClient[SyncFuture]) extends NebulaClient[Task] {

    def init(poolConfig: NebulaPoolConfig): Task[Boolean] =
      ZIO.fromFuture(_ => underlying.init(poolConfig))

    def close(): Task[Unit] = ZIO.attempt(underlying.close())

    def getSession(poolConfig: NebulaPoolConfig): Task[NebulaSession[Task]] =
      ZIO.fromFuture(_ => underlying.getSession(poolConfig).map(s => new NebulaSessionImpl(s)))

    def getSession(poolConfig: NebulaPoolConfig, useSpace: Boolean): Task[NebulaSession[Task]] =
      ZIO.fromFuture(_ => underlying.getSession(poolConfig, useSpace).map(s => new NebulaSessionImpl(s)))

    def activeConnNum: Task[Int] = ZIO.fromFuture(_ => underlying.activeConnNum)

    def idleConnNum: Task[Int] = ZIO.fromFuture(_ => underlying.idleConnNum)

    def waitersNum: Task[Int] = ZIO.fromFuture(_ => underlying.waitersNum)

  }

  val layer: ZLayer[Scope, Throwable, NebulaClient[Task]] =
    ZLayer.fromZIO(
      ZIO.acquireRelease(ZIO.attempt(new Impl(new NebulaClientDefault(new Pool))))(p =>
        p.close().onError(e => ZIO.logErrorCause(e)).ignoreLogged
      )
    )
}
