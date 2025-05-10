package nebula4scala.cats

import cats.effect._

import nebula4scala.SyncFuture
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.impl.NebulaSessionClientDefault

object NebulaSessionClient {

  private final class Impl[F[_]: Async](
    underlying: NebulaSessionClient[SyncFuture]
  ) extends NebulaSessionClient[F] {

    override def execute(stmt: String): F[NebulaResultSet] =
      Async[F].fromFuture(Async[F].delay(underlying.execute(stmt)))

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
      Async[F].delay(new Impl(NebulaSessionClientDefault.make(sessionPoolConfig)))
    )(client => client.close())
}
