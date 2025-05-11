package nebula4scala.cats.effect

import cats.effect.kernel.Async
import cats.syntax.all._

import com.vesoft.nebula.client.graph.data.HostAddress

import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.cats.effect.syntax._
import nebula4scala.data.input._
import nebula4scala.syntax._

final class NebulaSessionImpl[F[_]: Async](private val underlying: NebulaSession[ScalaFuture])
    extends NebulaSession[F] {

  def execute(stmt: Stmt): F[stmt.T] =
    implicitly[Effect[F]]
      .fromEffect(Async[F].blocking(underlying.execute(stmt)))
      .flatMap(result => implicitly[ResultSetHandler[F]].handle(result).asInstanceOf[F[stmt.T]])

  def ping(): F[Boolean] = implicitly[Effect[F]].fromEffect(Async[F].blocking(underlying.ping()))

  def pingSession(): F[Boolean] = implicitly[Effect[F]].fromEffect(Async[F].blocking(underlying.pingSession()))

  def release(): F[Unit] = implicitly[Effect[F]].fromEffect(Async[F].blocking(underlying.release()))

  def graphHost: F[HostAddress] = implicitly[Effect[F]].fromEffect(Async[F].blocking(underlying.graphHost))

  def sessionID: F[Long] = implicitly[Effect[F]].fromEffect(Async[F].blocking(underlying.sessionID))

  def close(): F[Unit] = implicitly[Effect[F]].fromEffect(Async[F].blocking(underlying.close()))

}
