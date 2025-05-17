package nebula4scala.cats.effect

import scala.util.Try

import cats.effect._
import cats.syntax.all._

import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.cats.effect.syntax._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.impl.NebulaSessionClientDefault
import nebula4scala.syntax._

object NebulaSessionClient {

  private final class Impl[F[_]: Async](
    underlying: NebulaSessionClient[Try]
  ) extends NebulaSessionClient[F] {

    def execute(stmt: Stmt): F[stmt.T] =
      implicitly[Effect[F]]
        .fromTry(
          underlying.execute(stmt)
        )
        .flatMap(result => implicitly[ResultSetHandler[F]].handle(result).asInstanceOf[F[stmt.T]])

    def idleSessionNum: F[Int] = implicitly[Effect[F]].fromTry(underlying.idleSessionNum)

    def isActive: F[Boolean] = implicitly[Effect[F]].fromTry(underlying.isActive)

    def isClosed: F[Boolean] = implicitly[Effect[F]].fromTry(underlying.isActive)

    def sessionNum: F[Int] = implicitly[Effect[F]].fromTry(underlying.sessionNum)

    def close(): F[Unit] = {
      implicitly[Effect[F]].fromTry(underlying.close())
    }
  }

  def resource[F[_]: Async](config: NebulaClientConfig): Resource[F, NebulaSessionClient[F]] =
    Resource.make(
      Async[F].blocking(new Impl(NebulaSessionClientDefault.make(config)))
    )(client => client.close())
}
