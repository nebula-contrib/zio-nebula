package nebula4scala.impl

import scala.collection.JavaConverters._
import scala.concurrent.Future

import com.vesoft.nebula.client.graph.{ NebulaPoolConfig => _ }
import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.graph.net.{ NebulaPool => Pool }

import nebula4scala.SyncFuture
import nebula4scala.api.{ NebulaClient, NebulaSession }
import nebula4scala.data._
import nebula4scala.data.input.Stmt

object NebulaClientDefault {
  def make: NebulaClient[SyncFuture] = new NebulaClientDefault(new Pool)

}

final class NebulaClientDefault(underlying: Pool) extends NebulaClient[SyncFuture] {

  def init(poolConfig: NebulaPoolConfig): SyncFuture[Boolean] =
    Future.successful(
      underlying.init(poolConfig.address.map(d => new HostAddress(d.host, d.port)).asJava, poolConfig.toJava)
    )

  def close(): SyncFuture[Unit] = Future.successful(underlying.close())

  def openSession(poolConfig: NebulaPoolConfig): SyncFuture[NebulaSession[SyncFuture]] =
    Future.successful(
      new NebulaSessionDefault(
        underlying.getSession(
          poolConfig.auth.username,
          poolConfig.auth.password,
          poolConfig.reconnect
        )
      )
    )

  def openSession(poolConfig: NebulaPoolConfig, useSpace: Boolean): SyncFuture[NebulaSession[SyncFuture]] =
    Future.successful {
      val session = new NebulaSessionDefault(
        underlying.getSession(
          poolConfig.auth.username,
          poolConfig.auth.password,
          poolConfig.reconnect
        )
      )
      if (useSpace && poolConfig.spaceName.nonEmpty) {
        session.execute(Stmt.str(s"USE `${poolConfig.spaceName.orNull}`"))
      }
      session
    }

  def activeConnNum: SyncFuture[Int] = Future.successful(underlying.getActiveConnNum)

  def idleConnNum: SyncFuture[Int] = Future.successful(underlying.getIdleConnNum)

  def waitersNum: SyncFuture[Int] = Future.successful(underlying.getWaitersNum)

}
