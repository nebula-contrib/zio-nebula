package nebula4scala.api

import nebula4scala.data._

trait NebulaClient[F[_]] {

  def init(poolConfig: NebulaPoolConfig): F[Boolean]

  def close(): F[Unit]

  def getSession(poolConfig: NebulaPoolConfig, useSpace: Boolean = false): F[NebulaSession[F]]

  def activeConnNum: F[Int]

  def idleConnNum: F[Int]

  def waitersNum: F[Int]

}
