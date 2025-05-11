package nebula4scala.api

import nebula4scala.data.input.Stmt

trait NebulaSessionClient[F[_]] {

  def execute(stmt: Stmt): F[stmt.T]

  def close(): F[Unit]

  def isActive: F[Boolean]

  def isClosed: F[Boolean]

  def sessionNum: F[Int]

  def idleSessionNum: F[Int]
}
