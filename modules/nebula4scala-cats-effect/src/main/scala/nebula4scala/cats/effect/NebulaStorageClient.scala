package nebula4scala.cats.effect

import scala.util.Try

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
    underlying: NebulaStorageClient[Try]
  ) extends NebulaStorageClient[F] {

    def connect(): F[Boolean] = implicitly[Effect[F]].fromTry(underlying.connect())

    def close(): F[Unit] = implicitly[Effect[F]].fromTry(underlying.close())

    def scan(scanInput: ScanInput): F[scanInput.T] =
      implicitly[Effect[F]].fromTry(underlying.scan(scanInput))

  }

  def resource[F[_]: Async](config: NebulaClientConfig): Resource[F, NebulaStorageClient[F]] = {
    Resource.make(
      Async[F].blocking(
        new Impl(NebulaStorageClientDefault.make(config))
      )
    )(client => client.close())
  }
}
