package nebula4scala.cats.effect

import scala.util.Try

import cats.syntax.all._

import _root_.cats.effect._
import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.cats.effect.syntax._
import nebula4scala.data._
import nebula4scala.impl.NebulaClientDefault

object NebulaClient {

  private final class Impl[F[_]: Async](
    underlying: NebulaClient[Try]
  ) extends NebulaClient[F] {

    def init(): F[Boolean] =
      implicitly[Effect[F]].fromTry(underlying.init())

    def close(): F[Unit] = implicitly[Effect[F]].fromTry(underlying.close())

    def getSession(useSpace: Boolean = false): F[NebulaSession[F]] =
      implicitly[Effect[F]]
        .fromTry(
          underlying
            .getSession(
              useSpace
            )
        )
        .map(s => new NebulaSessionImpl(s))

    def activeConnNum: F[Int] = implicitly[Effect[F]].fromTry(underlying.activeConnNum)

    def idleConnNum: F[Int] = implicitly[Effect[F]].fromTry(underlying.idleConnNum)

    def waitersNum: F[Int] = implicitly[Effect[F]].fromTry(underlying.waitersNum)
  }

  def resource[F[_]: Async](config: NebulaClientConfig): Resource[F, NebulaClient[F]] =
    Resource.make(
      Async[F].blocking(new Impl(NebulaClientDefault.make(config)))
    )(client => client.close())
}
