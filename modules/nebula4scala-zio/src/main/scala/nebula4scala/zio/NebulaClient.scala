package nebula4scala.zio

import com.vesoft.nebula.client.graph.{ NebulaPoolConfig => _ }
import com.vesoft.nebula.client.graph.net.{ NebulaPool => Pool }

import _root_.zio._
import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.impl.NebulaClientDefault
import nebula4scala.syntax._
import nebula4scala.zio.syntax._

object NebulaClient {

  private final class Impl(underlying: NebulaClient[ScalaFuture]) extends NebulaClient[Task] {

    def init(poolConfig: NebulaPoolConfig): Task[Boolean] =
      implicitly[Effect[Task]].fromFuture(underlying.init(poolConfig))

    def close(): Task[Unit] = implicitly[Effect[Task]].fromFuture(underlying.close())

    def getSession(poolConfig: NebulaPoolConfig, useSpace: Boolean = false): Task[NebulaSession[Task]] =
      implicitly[Effect[Task]]
        .fromFuture(underlying.getSession(poolConfig, useSpace))
        .map(s => new NebulaSessionImpl(s))

    def activeConnNum: Task[Int] = implicitly[Effect[Task]].fromFuture(underlying.activeConnNum)

    def idleConnNum: Task[Int] = implicitly[Effect[Task]].fromFuture(underlying.idleConnNum)

    def waitersNum: Task[Int] = implicitly[Effect[Task]].fromFuture(underlying.waitersNum)

  }

  val layer: ZLayer[Scope, Throwable, NebulaClient[Task]] =
    ZLayer.fromZIO(
      ZIO.acquireRelease(ZIO.attempt(new Impl(new NebulaClientDefault(new Pool))))(p =>
        p.close().onError(e => ZIO.logErrorCause(e)).ignoreLogged
      )
    )
}
