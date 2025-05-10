package nebula4scala.cats

import cats.effect.kernel.Async
import cats.syntax.all._

import com.vesoft.nebula.client.graph.data.HostAddress

import nebula4scala.api._
import nebula4scala.cats.syntax._
import nebula4scala.data.input._
import nebula4scala.syntax._

final class NebulaSessionImpl[F[_]: Async](private val underlying: NebulaSession[ScalaFuture])
    extends NebulaSession[F] {

  def execute(stmt: Stmt): F[stmt.T] =
    Async[F]
      .fromFuture(Async[F].blocking(underlying.execute(stmt)))
      .flatMap(result => implicitly[ResultSetHandler[F]].handle(result).asInstanceOf[F[stmt.T]])

  def ping(): F[Boolean] = Async[F].fromFuture(Async[F].blocking(underlying.ping()))

  def pingSession(): F[Boolean] = Async[F].fromFuture(Async[F].blocking(underlying.pingSession()))

  def release(): F[Unit] = Async[F].fromFuture(Async[F].blocking(underlying.release()))

  def graphHost: F[HostAddress] = Async[F].fromFuture(Async[F].blocking(underlying.graphHost))

  def sessionID: F[Long] = Async[F].fromFuture(Async[F].blocking(underlying.sessionID))

  def close(): F[Unit] = Async[F].fromFuture(Async[F].blocking(underlying.close()))

}
