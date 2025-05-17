package nebula4scala

import scala.util.Try

trait Effect[F[_]] {
  def fromTry[A](tryM: => Try[A]): F[A]
  def fromBlocking[A](tryMM: => F[() => Try[A]]): F[A]
}
