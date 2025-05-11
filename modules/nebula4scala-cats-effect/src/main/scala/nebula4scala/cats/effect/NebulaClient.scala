package nebula4scala.cats.effect

import cats.syntax.all._

import com.vesoft.nebula.client.graph.{ NebulaPoolConfig => _ }

import _root_.cats.effect._
import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.cats.effect.syntax._
import nebula4scala.data._
import nebula4scala.impl._
import nebula4scala.syntax._

object NebulaClient {

  private final class Impl[F[_]: Async](
    underlying: NebulaClient[ScalaFuture]
  ) extends NebulaClient[F] {

    def init(poolConfig: NebulaPoolConfig): F[Boolean] =
      implicitly[Effect[F]].fromFuture(underlying.init(poolConfig))

    def close(): F[Unit] = implicitly[Effect[F]].fromFuture(underlying.close())

    def getSession(poolConfig: NebulaPoolConfig, useSpace: Boolean): F[NebulaSession[F]] =
      implicitly[Effect[F]]
        .fromFuture(
          underlying
            .getSession(
              poolConfig,
              useSpace
            )
        )
        .map(s => new NebulaSessionImpl(s))

    def activeConnNum: F[Int] = implicitly[Effect[F]].fromFuture(underlying.activeConnNum)

    def idleConnNum: F[Int] = implicitly[Effect[F]].fromFuture(underlying.idleConnNum)

    def waitersNum: F[Int] = implicitly[Effect[F]].fromFuture(underlying.waitersNum)
  }

  def resource[F[_]: Async]: Resource[F, NebulaClient[F]] =
    Resource.make(
      Async[F].blocking(new Impl(NebulaClientDefault.make))
    )(client => client.close())
}
