package nebula4scala.impl

import scala.concurrent._
import scala.jdk.CollectionConverters._
import scala.util._

import com.vesoft.nebula.client.graph._
import com.vesoft.nebula.client.graph.data.HostAddress

import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.syntax._

object NebulaSessionClientDefault {

  def make(config: NebulaClientConfig): NebulaSessionClient[ScalaFuture] = {
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

final class NebulaSessionClientDefault(underlying: SessionPool) extends NebulaSessionClient[ScalaFuture] {

  override def execute(stmt: Stmt): ScalaFuture[stmt.T] = {
    val f = Future(stmt match {
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
    f.onComplete {
      case e @ Failure(exception) =>
        exception.printStackTrace()
      case s @ Success(value) => s
    }
    f
  }

  override def idleSessionNum: ScalaFuture[Int] = Future(underlying.getIdleSessionNums)

  override def sessionNum: ScalaFuture[Int] = Future(underlying.getSessionNums)

  override def isActive: ScalaFuture[Boolean] = Future(underlying.isActive)

  override def isClosed: ScalaFuture[Boolean] = Future(underlying.isClosed)

  override def close(): ScalaFuture[Unit] = Future(underlying.close())

}
