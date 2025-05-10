package nebula4scala

import _root_.cats.Monad
import _root_.cats.effect.Async
import nebula4scala.api.NebulaResultSet
import nebula4scala.data.input.Context
import nebula4scala.syntax._

package cats {

  object syntax {

    implicit def context[F[_]: Monad]: Context[F] = new Context[F] {}

    implicit def asyncHandler[F[_]: Async]: ResultSetHandler[F] = new ResultSetHandler[F] {

      def handle(result: Any): F[Any] = result match {
        case set: NebulaResultSet[ScalaFuture] @unchecked => Async[F].delay(new NebulaResultSetImpl(set))
        case str: String                                  => Async[F].delay(str)
        case other =>
          Async[F].raiseError(new IllegalArgumentException(s"Unexpected result type: ${other.getClass}"))
      }
    }
  }
}
