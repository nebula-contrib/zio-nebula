package nebula4scala.cats.effect

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
    underlying: NebulaSessionClient[ScalaFuture]
  ) extends NebulaSessionClient[F] {

    def execute(stmt: Stmt): F[stmt.T] =
      implicitly[Effect[F]]
        .fromFuture(
          underlying.execute(stmt)
        )
        .flatMap(result => implicitly[ResultSetHandler[F]].handle(result).asInstanceOf[F[stmt.T]])

    def idleSessionNum: F[Int] = implicitly[Effect[F]].fromFuture(underlying.idleSessionNum)

    def isActive: F[Boolean] = implicitly[Effect[F]].fromFuture(underlying.isActive)

    def isClosed: F[Boolean] = implicitly[Effect[F]].fromFuture(underlying.isActive)

    def sessionNum: F[Int] = implicitly[Effect[F]].fromFuture(underlying.sessionNum)

    def close(): F[Unit] = {
      implicitly[Effect[F]].fromFuture(underlying.close())
    }
  }

  def resource[F[_]: Async](config: NebulaClientConfig): Resource[F, NebulaSessionClient[F]] =
    Resource.make(
      Async[F].blocking(new Impl(NebulaSessionClientDefault.make(config)))
    )(client => client.close())
}
