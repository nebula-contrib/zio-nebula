package nebula4scala.impl

import scala.collection.JavaConverters._
import scala.concurrent.{ blocking, Future }

import com.vesoft.nebula.client.graph.{ NebulaPoolConfig => _ }
import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.graph.net.{ NebulaPool => Pool }

import nebula4scala.api.{ NebulaClient, NebulaSession }
import nebula4scala.data._
import nebula4scala.data.input.Stmt
import nebula4scala.syntax._

object NebulaClientDefault {
  def make: NebulaClient[ScalaFuture] = new NebulaClientDefault(new Pool)

}

final class NebulaClientDefault(underlying: Pool) extends NebulaClient[ScalaFuture] {

  def init(poolConfig: NebulaPoolConfig): ScalaFuture[Boolean] =
    Future(
      blocking(underlying.init(poolConfig.address.map(d => new HostAddress(d.host, d.port)).asJava, poolConfig.toJava))
    )

  def close(): ScalaFuture[Unit] = Future(underlying.close())

  def getSession(poolConfig: NebulaPoolConfig, useSpace: Boolean = false): ScalaFuture[NebulaSession[ScalaFuture]] =
    Future {
      val session = new NebulaSessionDefault(
        underlying.getSession(
          poolConfig.auth.username,
          poolConfig.auth.password,
          poolConfig.reconnect
        )
      )
      if (useSpace && poolConfig.spaceName.nonEmpty) {
        session.execute(Stmt.str[ScalaFuture](s"USE `${poolConfig.spaceName.orNull}`"))
      }
      session
    }

  def activeConnNum: ScalaFuture[Int] = Future(underlying.getActiveConnNum)

  def idleConnNum: ScalaFuture[Int] = Future(underlying.getIdleConnNum)

  def waitersNum: ScalaFuture[Int] = Future(underlying.getWaitersNum)

}
