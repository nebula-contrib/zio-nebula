package zio.nebula.net

import scala.jdk.CollectionConverters._

import zio._
import zio.nebula._

import com.vesoft.nebula.client.graph.{ NebulaPoolConfig => _ }
import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.graph.net.{ NebulaPool => NebulaPl }

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/29
 */
private[nebula] final class NebulaClientLive(underlying: NebulaPl) extends NebulaClient {

  def init(): ZIO[NebulaPoolConfig, Throwable, Boolean] =
    for {
      config <- ZIO.service[NebulaPoolConfig]
      status <-
        ZIO.attempt(
          underlying.init(config.address.map(d => new HostAddress(d.host, d.port)).asJava, config.toJava)
        )
    } yield status

  def close(): Task[Unit] = ZIO.attempt(underlying.close())

  def openSession(poolConfig: NebulaPoolConfig): ZIO[Any, Throwable, NebulaSession] =
    for {
      session <- ZIO.attempt(
                   new NebulaSession(
                     underlying.getSession(
                       poolConfig.auth.username,
                       poolConfig.auth.password,
                       poolConfig.reconnect
                     )
                   )
                 )
    } yield session

  def openSession(useSpace: Boolean): ZIO[NebulaPoolConfig, Throwable, NebulaSession] =
    for {
      poolConfig <- ZIO.service[NebulaPoolConfig]
      session    <- ZIO.attempt(
                      new NebulaSession(
                        underlying.getSession(
                          poolConfig.auth.username,
                          poolConfig.auth.password,
                          poolConfig.reconnect
                        )
                      )
                    )
      _          <- ZIO.when(useSpace && poolConfig.spaceName.nonEmpty) {
                      session.execute(Stmt.str(s"USE `${poolConfig.spaceName.orNull}`"))
                    }

    } yield session

  def activeConnNum: Task[Int] = ZIO.attempt(underlying.getActiveConnNum)

  def idleConnNum: Task[Int] = ZIO.attempt(underlying.getIdleConnNum)

  def waitersNum: Task[Int] = ZIO.attempt(underlying.getWaitersNum)

}
