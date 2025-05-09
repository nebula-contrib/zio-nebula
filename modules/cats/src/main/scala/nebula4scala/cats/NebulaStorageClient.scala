package nebula4scala.cats

import cats.effect.{ Async, Resource }

import nebula4scala.SyncFuture
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.impl.NebulaStorageClientDefault

object NebulaStorageClient {

  private final class Impl[F[_]: Async](
    underlying: NebulaStorageClient[SyncFuture]
  ) extends NebulaStorageClient[F] {

    override def connect(): F[Boolean] = Async[F].fromFuture(Async[F].delay(underlying.connect()))

    override def close(): F[Unit] = Async[F].fromFuture(Async[F].delay(underlying.close()))

    override def scan(scanInput: ScanInput): F[scanInput.T] = {
      Async[F].fromFuture(Async[F].delay(underlying.scan(scanInput)))
    }

  }

  def resource[F[_]: Async](config: NebulaStorageConfig): Resource[F, NebulaStorageClient[F]] = {
    Resource.make(
      Async[F].delay(
        new Impl(NebulaStorageClientDefault.make(config))
      )
    )(client => client.close())
  }
}
