package nebula4scala.api

trait NebulaClient[F[_]] {

  def init(): F[Boolean]

  def close(): F[Unit]

  def getSession(useSpace: Boolean = false): F[NebulaSession[F]]

  def activeConnNum: F[Int]

  def idleConnNum: F[Int]

  def waitersNum: F[Int]

}
