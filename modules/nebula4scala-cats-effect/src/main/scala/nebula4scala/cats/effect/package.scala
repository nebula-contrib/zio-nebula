package nebula4scala.cats.effect

import scala.util.Try

import _root_.cats.Monad
import _root_.cats.effect.Async
import _root_.cats.syntax.all._
import nebula4scala.Effect
import nebula4scala.api.NebulaResultSet
import nebula4scala.data.input.Context
import nebula4scala.syntax._

object syntax {

  implicit def context[F[_]: Monad]: Context[F] = new Context[F] {}

  implicit def asyncHandler[F[_]: Async]: ResultSetHandler[F] = new ResultSetHandler[F] {

    def handle(result: Any): F[Any] = result match {
      case set: NebulaResultSet[Try] @unchecked => Async[F].delay(new NebulaResultSetImpl(set))
      case str: String                          => Async[F].delay(str)
      case other                                =>
        Async[F].raiseError(new IllegalArgumentException(s"Unexpected result type: ${other.getClass}"))
    }
  }

  implicit def catsEffect[F[_]: Async]: Effect[F] = new Effect[F] {
    def fromTry[A](tryM: => Try[A]): F[A] = Async[F].fromTry(tryM)

    def fromBlocking[A](tryMM: => F[() => Try[A]]): F[A] = {
      Async[F].blocking(tryMM.flatMap(t => Async[F].fromTry(t()))).flatten
    }
  }
}
