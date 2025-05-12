package nebula4scala.zio

import zio._

import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.data.NebulaHostAddress
import nebula4scala.data.input._
import nebula4scala.syntax._
import nebula4scala.zio.syntax._

final class NebulaSessionImpl(private val underlying: NebulaSession[ScalaFuture]) extends NebulaSession[Task] {

  def execute(stmt: Stmt): Task[stmt.T] = {
    ZIO.blocking(implicitly[Effect[Task]].fromFuture(underlying.execute(stmt)).flatMap { result =>
      implicitly[ResultSetHandler[Task]].handle(result).asInstanceOf[Task[stmt.T]]
    })
  }

  def ping(): Task[Boolean] = ZIO.blocking(implicitly[Effect[Task]].fromFuture(underlying.ping()))

  def pingSession(): Task[Boolean] = ZIO.blocking(implicitly[Effect[Task]].fromFuture(underlying.pingSession()))

  def release(): Task[Unit] = ZIO.blocking(implicitly[Effect[Task]].fromFuture(underlying.release()))

  def graphHost: Task[NebulaHostAddress] = ZIO.blocking(implicitly[Effect[Task]].fromFuture(underlying.graphHost))

  def sessionID: Task[Long] = ZIO.blocking(implicitly[Effect[Task]].fromFuture(underlying.sessionID))

  def close(): Task[Unit] = ZIO.blocking(implicitly[Effect[Task]].fromFuture(underlying.close()))

}
