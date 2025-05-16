package nebula4scala.zio

import scala.util.Try

import _root_.zio._
import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.impl.NebulaSessionClientDefault
import nebula4scala.syntax._
import nebula4scala.zio.syntax._

object NebulaSessionClient {

  private final class Impl(underlying: NebulaSessionClient[Try]) extends NebulaSessionClient[Task] {

    def execute(stmt: Stmt): Task[stmt.T] =
      implicitly[Effect[Task]].fromTry(underlying.execute(stmt)).flatMap { result =>
        implicitly[ResultSetHandler[Task]].handle(result).asInstanceOf[Task[stmt.T]]
      }

    def idleSessionNum: Task[Int] = implicitly[Effect[Task]].fromTry(underlying.idleSessionNum)

    def sessionNum: Task[Int] = implicitly[Effect[Task]].fromTry(underlying.sessionNum)

    def isActive: Task[Boolean] = implicitly[Effect[Task]].fromTry(underlying.isActive)

    def isClosed: Task[Boolean] = implicitly[Effect[Task]].fromTry(underlying.isClosed)

    def close(): Task[Unit] = implicitly[Effect[Task]].fromTry(underlying.close())

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
