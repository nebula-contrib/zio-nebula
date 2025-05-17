package nebula4scala.impl.future

import scala.concurrent.Future
import scala.util.Try

import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.impl.NebulaSessionClientDefault
import nebula4scala.impl.future.syntax._
import nebula4scala.syntax._

object NebulaSessionClientFuture {

  def make(config: NebulaClientConfig): NebulaSessionClient[Future] = {
    new Impl(NebulaSessionClientDefault.make(config))
  }

  private final class Impl(underlying: NebulaSessionClient[Try]) extends NebulaSessionClient[Future] {

    def execute(stmt: Stmt): Future[stmt.T] =
      implicitly[Effect[Future]].fromTry(underlying.execute(stmt)).flatMap { result =>
        implicitly[ResultSetHandler[Future]].handle(result).asInstanceOf[Future[stmt.T]]
      }

    def idleSessionNum: Future[Int] = implicitly[Effect[Future]].fromTry(underlying.idleSessionNum)

    def sessionNum: Future[Int] = implicitly[Effect[Future]].fromTry(underlying.sessionNum)

    def isActive: Future[Boolean] = implicitly[Effect[Future]].fromTry(underlying.isActive)

    def isClosed: Future[Boolean] = implicitly[Effect[Future]].fromTry(underlying.isClosed)

    def close(): Future[Unit] = implicitly[Effect[Future]].fromTry(underlying.close())

  }

}
