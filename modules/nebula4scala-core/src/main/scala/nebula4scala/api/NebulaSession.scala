package nebula4scala.api

import com.vesoft.nebula.client.graph.data.HostAddress

import nebula4scala.data.input.Stmt

trait NebulaSession[F[_]] {

  def execute(stmt: Stmt): F[stmt.T]

  def ping(): F[Boolean]

  def pingSession(): F[Boolean]

  def release(): F[Unit]

  def graphHost: F[HostAddress]

  def sessionID: F[Long]

  def close(): F[Unit]
}
