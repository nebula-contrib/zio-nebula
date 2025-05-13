package nebula4scala.impl

import scala.collection.JavaConverters._
import scala.util.Try

import com.vesoft.nebula.client.graph.{ NebulaPoolConfig => _ }
import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.graph.net.{ NebulaPool => Pool }

import nebula4scala.api.{ NebulaClient, NebulaSession }
import nebula4scala.data._
import nebula4scala.data.input.Stmt
import nebula4scala.syntax._

object NebulaClientDefault {
  def make(config: NebulaClientConfig): NebulaClient[Try] = new NebulaClientDefault(config, new Pool)
}

final class NebulaClientDefault(config: NebulaClientConfig, underlying: Pool) extends NebulaClient[Try] {

  def init(): Try[Boolean] =
    Try(
      underlying.init(config.graph.address.map(d => new HostAddress(d.host, d.port)).asJava, config.graph.pool.toJava)
    )

  def close(): Try[Unit] = Try(underlying.close())

  def getSession(useSpace: Boolean = false): Try[NebulaSession[Try]] =
    Try {
      val session = new NebulaSessionDefault(
        underlying.getSession(
          config.graph.auth.username,
          config.graph.auth.password,
          config.graph.reconnect
        )
      )
      if (useSpace && config.graph.spaceName.nonEmpty) {
        session.execute(Stmt.str[Try](s"USE `${config.graph.spaceName}`"))
      }
      session
    }

  def activeConnNum: Try[Int] = Try(underlying.getActiveConnNum)

  def idleConnNum: Try[Int] = Try(underlying.getIdleConnNum)

  def waitersNum: Try[Int] = Try(underlying.getWaitersNum)

}
