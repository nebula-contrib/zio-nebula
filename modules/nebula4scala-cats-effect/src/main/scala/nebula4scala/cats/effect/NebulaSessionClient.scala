package nebula4scala.cats.effect

import cats.effect._
import cats.syntax.all._

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

    override def execute(stmt: Stmt): F[stmt.T] =
      Async[F]
        .fromFuture(
          Async[F].delay(
            underlying.execute(stmt)
          )
        )
        .flatMap(result => implicitly[ResultSetHandler[F]].handle(result).asInstanceOf[F[stmt.T]])

    override def idleSessionNum: F[Int] = Async[F].fromFuture(Async[F].delay(underlying.idleSessionNum))

    override def isActive: F[Boolean] = Async[F].fromFuture(Async[F].delay(underlying.isActive))

    override def isClosed: F[Boolean] = Async[F].fromFuture(Async[F].delay(underlying.isActive))

    override def sessionNum: F[Int] = Async[F].fromFuture(Async[F].delay(underlying.sessionNum))

    override def close(): F[Unit] = {
      Async[F].delay(underlying.close())
    }
  }

  def resource[F[_]: Async](sessionPoolConfig: NebulaSessionPoolConfig): Resource[F, NebulaSessionClient[F]] =
    Resource.make(
      Async[F].blocking(new Impl(NebulaSessionClientDefault.make(sessionPoolConfig)))
    )(client => client.close())
}
