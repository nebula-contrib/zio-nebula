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
  def make(config: NebulaClientConfig): NebulaClient[ScalaFuture] = new NebulaClientDefault(config, new Pool)

}

final class NebulaClientDefault(config: NebulaClientConfig, underlying: Pool) extends NebulaClient[ScalaFuture] {

  def init(): ScalaFuture[Boolean] =
    Future(
      blocking(
        underlying.init(config.graph.address.map(d => new HostAddress(d.host, d.port)).asJava, config.graph.pool.toJava)
      )
    )

  def close(): ScalaFuture[Unit] = Future(underlying.close())

  def getSession(useSpace: Boolean = false): ScalaFuture[NebulaSession[ScalaFuture]] =
    Future {
      val session = new NebulaSessionDefault(
        underlying.getSession(
          config.graph.auth.username,
          config.graph.auth.password,
          config.graph.reconnect
        )
      )
      if (useSpace && config.graph.spaceName.nonEmpty) {
        session.execute(Stmt.str[ScalaFuture](s"USE `${config.graph.spaceName}`"))
      }
      session
    }

  def activeConnNum: ScalaFuture[Int] = Future(underlying.getActiveConnNum)

  def idleConnNum: ScalaFuture[Int] = Future(underlying.getIdleConnNum)

  def waitersNum: ScalaFuture[Int] = Future(underlying.getWaitersNum)

}
