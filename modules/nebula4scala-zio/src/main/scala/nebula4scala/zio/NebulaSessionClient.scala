package nebula4scala.zio

import _root_.zio._
import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.impl._
import nebula4scala.syntax._
import nebula4scala.zio.syntax._

object NebulaSessionClient {

  private final class Impl(underlying: NebulaSessionClient[ScalaFuture]) extends NebulaSessionClient[Task] {

    def execute(stmt: Stmt): Task[stmt.T] =
      implicitly[Effect[Task]].fromFuture(underlying.execute(stmt)).flatMap { result =>
        implicitly[ResultSetHandler[Task]].handle(result).asInstanceOf[Task[stmt.T]]
      }

    def idleSessionNum: Task[Int] = implicitly[Effect[Task]].fromFuture(underlying.idleSessionNum)

    def sessionNum: Task[Int] = implicitly[Effect[Task]].fromFuture(underlying.sessionNum)

    def isActive: Task[Boolean] = implicitly[Effect[Task]].fromFuture(underlying.isActive)

    def isClosed: Task[Boolean] = implicitly[Effect[Task]].fromFuture(underlying.isClosed)

    def close(): Task[Unit] = implicitly[Effect[Task]].fromFuture(underlying.close())

  }

  val layer: ZLayer[Scope & NebulaClientConfig, Throwable, NebulaSessionClient[Task]] = ZLayer.fromZIO {
    for {
      config <- ZIO.service[NebulaClientConfig]
      sessionPool <- ZIO.acquireRelease(
        ZIO.attemptBlocking(new Impl(NebulaSessionClientDefault.make(config)))
      )(release => release.close().onError(e => ZIO.logErrorCause(e)).ignoreLogged)
    } yield sessionPool
  }
}
