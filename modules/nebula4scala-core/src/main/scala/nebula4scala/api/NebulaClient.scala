package nebula4scala.api

import nebula4scala.data._

trait NebulaClient[F[_]] {

  def init(poolConfig: NebulaPoolConfig): F[Boolean]

  /** close the client
   */
  def close(): F[Unit]

  /** init the client and execute `USE spaceName` if exists
   */
  def getSession(poolConfig: NebulaPoolConfig, useSpace: Boolean): F[NebulaSession[F]]

  /** init the client by using poolConfig
   */
  def getSession(poolConfig: NebulaPoolConfig): F[NebulaSession[F]]

  def activeConnNum: F[Int]

  def idleConnNum: F[Int]

  def waitersNum: F[Int]

}
