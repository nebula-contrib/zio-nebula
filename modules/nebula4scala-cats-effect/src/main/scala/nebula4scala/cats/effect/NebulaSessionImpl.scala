package nebula4scala.cats.effect

import scala.util.Try

import cats.effect.kernel.Async
import cats.syntax.all._

import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.cats.effect.syntax._
import nebula4scala.data.NebulaHostAddress
import nebula4scala.data.input._
import nebula4scala.syntax._

final class NebulaSessionImpl[F[_]: Async](private val underlying: NebulaSession[Try]) extends NebulaSession[F] {

  def execute(stmt: Stmt): F[stmt.T] =
    implicitly[Effect[F]]
      .fromBlocking(Async[F].delay(() => underlying.execute(stmt)))
      .flatMap(result => implicitly[ResultSetHandler[F]].handle(result).asInstanceOf[F[stmt.T]])

  def ping(): F[Boolean] = implicitly[Effect[F]].fromBlocking(Async[F].delay(() => underlying.ping()))

  def pingSession(): F[Boolean] = implicitly[Effect[F]].fromBlocking(Async[F].delay(() => underlying.pingSession()))

  def release(): F[Unit] = implicitly[Effect[F]].fromBlocking(Async[F].delay(() => underlying.release()))

  def graphHost: F[NebulaHostAddress] = implicitly[Effect[F]].fromBlocking(Async[F].delay(() => underlying.graphHost))

  def sessionID: F[Long] = implicitly[Effect[F]].fromBlocking(Async[F].delay(() => underlying.sessionID))

  def close(): F[Unit] = implicitly[Effect[F]].fromBlocking(Async[F].delay(() => underlying.close()))

}
