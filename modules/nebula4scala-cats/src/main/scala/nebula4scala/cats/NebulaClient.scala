package nebula4scala.cats

import cats.syntax.all._

import com.vesoft.nebula.client.graph.{ NebulaPoolConfig => _ }

import _root_.cats.effect._
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.impl._
import nebula4scala.syntax._

object NebulaClient {

  private final class Impl[F[_]: Async](
    underlying: NebulaClient[ScalaFuture]
  ) extends NebulaClient[F] {

    override def init(poolConfig: NebulaPoolConfig): F[Boolean] =
      Async[F].fromFuture(Async[F].delay(underlying.init(poolConfig)))

    override def close(): F[Unit] = Async[F].fromFuture(Async[F].delay(underlying.close()))

    override def getSession(poolConfig: NebulaPoolConfig, useSpace: Boolean): F[NebulaSession[F]] =
      Async[F]
        .fromFuture(
          Async[F].delay(
            underlying
              .getSession(
                poolConfig,
                useSpace
              )
          )
        )
        .map(s => new NebulaSessionImpl(s))

    override def getSession(poolConfig: NebulaPoolConfig): F[NebulaSession[F]] =
      Async[F]
        .fromFuture(
          Async[F].delay(
            underlying
              .getSession(
                poolConfig
              )
          )
        )
        .map(s => new NebulaSessionImpl(s))

    override def activeConnNum: F[Int] = Async[F].fromFuture(Async[F].delay(underlying.activeConnNum))

    override def idleConnNum: F[Int] = Async[F].fromFuture(Async[F].delay(underlying.idleConnNum))

    override def waitersNum: F[Int] = Async[F].fromFuture(Async[F].delay(underlying.waitersNum))
  }

  def resource[F[_]: Async]: Resource[F, NebulaClient[F]] =
    Resource.make(
      Async[F].blocking(new Impl(NebulaClientDefault.make))
    )(client => client.close())
}
