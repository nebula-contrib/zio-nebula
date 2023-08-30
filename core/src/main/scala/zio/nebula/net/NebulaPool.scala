package zio.nebula.net

import zio.*
import zio.nebula.*
import zio.nebula.net.NebulaSession

import com.vesoft.nebula.client.graph.NebulaPoolConfig
import com.vesoft.nebula.client.graph.net.NebulaPool as Pool

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/29
 */
trait NebulaPool {

  def init(config: NebulaPoolConfig): ZIO[NebulaConfig, Throwable, Boolean]

  def close(): Task[Unit]

  def getSession: ZIO[Scope & NebulaConfig, Throwable, NebulaSession]

  def getActiveConnNum: Task[Int]

  def getIdleConnNum: Task[Int]

  def getWaitersNum: Task[Int]

}

object NebulaPool {

  private def makePool: ZIO[Scope, Nothing, Pool] = ZIO.acquireRelease(ZIO.succeed(new Pool))(d =>
    ZIO.attempt(d.close()).onError(e => ZIO.logErrorCause(e)).ignoreLogged
  )

  lazy val layer: ZLayer[Scope, Nothing, NebulaPool] =
    ZLayer.fromZIO(makePool.map(pool => NebulaPoolLive(pool)))
}
