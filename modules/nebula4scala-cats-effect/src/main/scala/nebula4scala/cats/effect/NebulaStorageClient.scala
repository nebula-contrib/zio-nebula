package nebula4scala.cats.effect

import cats.effect._

import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.cats.effect.syntax._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.impl.NebulaStorageClientDefault
import nebula4scala.syntax._

object NebulaStorageClient {

  private final class Impl[F[_]: Async](
    underlying: NebulaStorageClient[ScalaFuture]
  ) extends NebulaStorageClient[F] {

    def connect(): F[Boolean] = implicitly[Effect[F]].fromFuture(underlying.connect())

    def close(): F[Unit] = implicitly[Effect[F]].fromFuture(underlying.close())

    def scan(scanInput: ScanInput): F[scanInput.T] =
      implicitly[Effect[F]].fromFuture(underlying.scan(scanInput))

  }

  def resource[F[_]: Async](config: NebulaClientConfig): Resource[F, NebulaStorageClient[F]] = {
    Resource.make(
      Async[F].blocking(
        new Impl(NebulaStorageClientDefault.make(config))
      )
    )(client => client.close())
  }
}
