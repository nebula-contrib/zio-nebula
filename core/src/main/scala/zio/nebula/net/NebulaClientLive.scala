package zio.nebula.net

import scala.jdk.CollectionConverters._

import zio._
import zio.nebula._
import zio.nebula.NebulaPoolConfig

import com.vesoft.nebula.client.graph.{ NebulaPoolConfig => _ }
import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.graph.net.{ NebulaPool => NebulaPl }

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/29
 */
private[nebula] final class NebulaClientLive(underlying: NebulaPl) extends NebulaClient {

  def init(): ZIO[NebulaSessionConfig & NebulaPoolConfig, Throwable, Boolean] =
    for {
      config <- ZIO.service[NebulaPoolConfig]
      status <-
        ZIO.serviceWithZIO[NebulaSessionConfig](sessionConfig =>
          ZIO.attemptBlocking(
            underlying.init(sessionConfig.address.map(d => new HostAddress(d.host, d.port)).asJava, config.toJava)
          )
        )
    } yield status

  def close(): Task[Unit] = ZIO.attempt(underlying.close())

  def getSession: ZIO[Scope & NebulaSessionConfig, Throwable, NebulaSession] =
    for {
      sessionConfig <- ZIO.service[NebulaSessionConfig]
      session       <-
        ZIO.acquireRelease(
          ZIO.attempt(
            new NebulaSession(
              underlying.getSession(
                sessionConfig.auth.username,
                sessionConfig.auth.password,
                sessionConfig.reconnect
              )
            )
          )
        )(_.close().onError(e => ZIO.logErrorCause(e)).ignoreLogged)
    } yield session

  def getActiveConnNum: Task[Int] = ZIO.attempt(underlying.getActiveConnNum)

  def getIdleConnNum: Task[Int] = ZIO.attempt(underlying.getIdleConnNum)

  def getWaitersNum: Task[Int] = ZIO.attempt(underlying.getWaitersNum)

}
