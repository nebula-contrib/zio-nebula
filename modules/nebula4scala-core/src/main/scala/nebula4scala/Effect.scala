package nebula4scala

import nebula4scala.syntax._

trait Effect[F[_]] {
  def fromFuture[A](future: => ScalaFuture[A]): F[A]
  def fromEffect[A](future: => F[ScalaFuture[A]]): F[A]
}
