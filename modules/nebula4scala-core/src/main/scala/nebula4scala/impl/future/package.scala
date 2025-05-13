package nebula4scala.impl

import scala.concurrent._
import scala.util.Try

import nebula4scala.Effect
import nebula4scala.api.NebulaResultSet
import nebula4scala.data.input.Context
import nebula4scala.syntax.ResultSetHandler

package object future {

  object syntax {

    implicit val ec: ExecutionContext       = scala.concurrent.ExecutionContext.Implicits.global
    implicit val futureCtx: Context[Future] = new Context[Future] {}

    implicit val futureHandler: ResultSetHandler[Future] = new ResultSetHandler[Future] {

      def handle(result: Any): Future[Any] = result match {
        case set: NebulaResultSet[Try] @unchecked => Future.successful(new NebulaResultSetFuture(set))
        case str: String                          => Future.successful(str)
        case other => Future.failed(new IllegalArgumentException(s"Unexpected result type: ${other.getClass}"))
      }
    }

    implicit val futureEffect: Effect[Future] = new Effect[Future] {
      def fromTry[A](tryM: => Try[A]): Future[A]                     = Future.fromTry(tryM)
      def fromBlocking[A](tryMM: => Future[() => Try[A]]): Future[A] = tryMM.flatMap(t => Future.fromTry(blocking(t())))
    }
  }
}
