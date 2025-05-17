package nebula4scala.impl.future

import scala.concurrent.Future
import scala.util.Try

import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.data.NebulaHostAddress
import nebula4scala.data.input._
import nebula4scala.impl.future.syntax._
import nebula4scala.syntax._

final class NebulaSessionFuture(private val underlying: NebulaSession[Try]) extends NebulaSession[Future] {

  def execute(stmt: Stmt): Future[stmt.T] = {
    implicitly[Effect[Future]].fromTry(underlying.execute(stmt)).flatMap { result =>
      implicitly[ResultSetHandler[Future]].handle(result).asInstanceOf[Future[stmt.T]]
    }
  }

  def ping(): Future[Boolean] = implicitly[Effect[Future]].fromBlocking(Future(() => underlying.ping()))

  def pingSession(): Future[Boolean] = implicitly[Effect[Future]].fromBlocking(Future(() => underlying.pingSession()))

  def release(): Future[Unit] = implicitly[Effect[Future]].fromBlocking(Future(() => underlying.release()))

  def graphHost: Future[NebulaHostAddress] = implicitly[Effect[Future]].fromBlocking(Future(() => underlying.graphHost))

  def sessionID: Future[Long] = implicitly[Effect[Future]].fromBlocking(Future(() => underlying.sessionID))

  def close(): Future[Unit] = implicitly[Effect[Future]].fromBlocking(Future(() => underlying.close()))

}
