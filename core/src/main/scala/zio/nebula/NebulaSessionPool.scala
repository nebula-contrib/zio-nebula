package zio.nebula

import scala.jdk.CollectionConverters.*

import zio.*
import zio.nebula.NebulaResultSet

import com.vesoft.nebula.client.graph.*
import com.vesoft.nebula.client.graph.data.HostAddress

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/29
 */
trait NebulaSessionPool {

  /**
   * init the SessionPool
   */
  def init(): Task[Boolean]

  /**
   * Execute the nGql sentence.
   * @param stmt
   *   The nGql sentence. such as insert ngql `INSERT VERTEX person(name) VALUES "Tom":("Tom");`
   * @return
   */
  def execute(stmt: String): Task[NebulaResultSet]

  /**
   * close the session pool
   */
  def close(): Task[Unit]

  /**
   * if the SessionPool has been initialized
   */
  def isActive: Task[Boolean]

  /**
   * if the SessionPool is closed
   */
  def isClosed: Task[Boolean]

  /**
   * get the number of all Session
   */
  def getSessionNums: Task[Int]

  /**
   * get the number of idle Session
   */
  def getIdleSessionNums: Task[Int]
}

object NebulaSessionPool {

  private def makeSession: ZIO[NebulaConfig with Scope, Nothing, SessionPool] =
    ZIO.serviceWithZIO[NebulaConfig](nebulaSessionConfig =>
      ZIO.acquireRelease(
        ZIO.succeed(
          new SessionPool(
            new SessionPoolConfig(
              nebulaSessionConfig.address.map(d => new HostAddress(d.host, d.port)).asJava,
              nebulaSessionConfig.auth.spaceName,
              nebulaSessionConfig.auth.username,
              nebulaSessionConfig.auth.password
            )
          )
        )
      )(release => ZIO.attempt(release.close()).onError(e => ZIO.logErrorCause(e)).ignoreLogged)
    )

  lazy val layer: ZLayer[NebulaConfig with Scope, Nothing, NebulaSessionPool] =
    ZLayer.fromZIO(
      ZIO.acquireRelease(makeSession.map(c => new NebulaSessionPoolLive(c)))(release =>
        release.close().onError(e => ZIO.logErrorCause(e)).ignoreLogged
      )
    )
}
