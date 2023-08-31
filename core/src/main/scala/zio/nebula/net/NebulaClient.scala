package zio.nebula.net

import zio._
import zio.nebula._
import zio.nebula.NebulaPoolConfig

import com.vesoft.nebula.client.graph.{ NebulaPoolConfig => _ }
import com.vesoft.nebula.client.graph.net.{ NebulaPool => Pool }

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/29
 */
trait NebulaClient {

  def init(): ZIO[NebulaSessionConfig & NebulaPoolConfig, Throwable, Boolean]

  def close(): Task[Unit]

  def getSession: ZIO[Scope & NebulaSessionConfig, Throwable, NebulaSession]

  def getActiveConnNum: Task[Int]

  def getIdleConnNum: Task[Int]

  def getWaitersNum: Task[Int]

}

object NebulaClient {

  private def makePool: ZIO[Scope, Nothing, Pool] = ZIO.acquireRelease(ZIO.succeed(new Pool))(d =>
    ZIO.attempt(d.close()).onError(e => ZIO.logErrorCause(e)).ignoreLogged
  )

  lazy val layer: ZLayer[Scope, Nothing, NebulaClient] =
    ZLayer.fromZIO(makePool.map(pool => new NebulaClientLive(pool)))
}
