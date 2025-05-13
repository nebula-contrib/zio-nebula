package nebula4scala.impl

import scala.concurrent._
import scala.jdk.CollectionConverters._
import scala.util._

import com.vesoft.nebula.client.graph._
import com.vesoft.nebula.client.graph.data.HostAddress

import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.impl.future.syntax._
import nebula4scala.syntax._

object NebulaSessionClientDefault {

  def make(config: NebulaClientConfig): NebulaSessionClient[Try] = {
    try {
      val sessionPool = new SessionPool(
        new SessionPoolConfig(
          config.graph.address.map(d => new HostAddress(d.host, d.port)).asJava,
          config.graph.spaceName,
          config.graph.auth.username,
          config.graph.auth.password
        ).setMaxSessionSize(config.graph.maxSessionSize)
          .setMinSessionSize(config.graph.minSessionSize)
          .setRetryTimes(config.graph.retryTimes)
          .setWaitTime(config.graph.waitTimeMills)
          .setIntervalTime(config.graph.intervalTimeMills)
          .setTimeout(config.graph.timeoutMills)
          .setCleanTime(config.graph.cleanTimeSeconds)
          .setReconnect(config.graph.reconnect)
          .setHealthCheckTime(config.graph.healthCheckTimeSeconds)
          .setUseHttp2(config.graph.useHttp2)
      )
      new NebulaSessionClientDefault(sessionPool)
    } catch {
      case e: Throwable =>
        e.printStackTrace()
        throw e
    }

  }
}

final class NebulaSessionClientDefault(underlying: SessionPool) extends NebulaSessionClient[Try] {

  override def execute(stmt: Stmt): Try[stmt.T] = {
    val f = Try(stmt match {
      case StringStmt(_stmt) =>
        new NebulaResultSetDefault(underlying.execute(_stmt)).asInstanceOf[stmt.T]
      case StringStmtWithMap(_stmt, parameterMap) =>
        new NebulaResultSetDefault(underlying.execute(_stmt, parameterMap.asJava)).asInstanceOf[stmt.T]
      case JsonStmt(jsonStmt) =>
        underlying
          .executeJson(jsonStmt)
          .asInstanceOf[stmt.T]
      case JsonStmtWithMap(jsonStmt, parameterMap) =>
        underlying
          .executeJsonWithParameter(jsonStmt, parameterMap.asJava)
          .asInstanceOf[stmt.T]
    })
    f match {
      case Failure(exception) =>
        exception.printStackTrace()
        throw exception
      case Success(value) => f
    }
  }

  override def idleSessionNum: Try[Int] = Try(underlying.getIdleSessionNums)

  override def sessionNum: Try[Int] = Try(underlying.getSessionNums)

  override def isActive: Try[Boolean] = Try(underlying.isActive)

  override def isClosed: Try[Boolean] = Try(underlying.isClosed())

  override def close(): Try[Unit] = Try(underlying.close())

}
