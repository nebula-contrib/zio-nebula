package nebula4scala.zio

import _root_.zio._
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.impl._
import nebula4scala.syntax._

object NebulaSessionClient {

  private final class Impl(underlying: NebulaSessionClient[SyncFuture]) extends NebulaSessionClient[Task] {

    override def execute(stmt: Stmt): Task[stmt.T] =
      ZIO
        .fromFuture(_ => underlying.execute(stmt))
        .map {
          case set: NebulaResultSet[_] =>
            new NebulaResultSetImpl(set.asInstanceOf[NebulaResultSet[SyncFuture]])
          case str: String => str
        }
        .map(_.asInstanceOf[stmt.T])

    override def idleSessionNum: Task[Int] = ZIO.fromFuture(_ => underlying.idleSessionNum)

    override def sessionNum: Task[Int] = ZIO.fromFuture(_ => underlying.sessionNum)

    override def isActive: Task[Boolean] = ZIO.fromFuture(_ => underlying.isActive)

    override def isClosed: Task[Boolean] = ZIO.fromFuture(_ => underlying.isClosed)

    override def close(): Task[Unit] = ZIO.fromFuture(_ => underlying.close())

  }

  val layer: ZLayer[Scope & NebulaSessionPoolConfig, Throwable, NebulaSessionClient[Task]] = ZLayer.fromZIO {
    for {
      nebulaConfig <- ZIO.service[NebulaSessionPoolConfig]
      sessionPool <- ZIO.acquireRelease(
        ZIO.attemptBlocking(new Impl(NebulaSessionClientDefault.make(nebulaConfig)))
      )(release => release.close().onError(e => ZIO.logErrorCause(e)).ignoreLogged)
    } yield sessionPool
  }
}
