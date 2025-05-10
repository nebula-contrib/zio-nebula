package nebula4scala.impl

import scala.concurrent.Future
import scala.jdk.CollectionConverters._

import com.vesoft.nebula.client.graph._
import com.vesoft.nebula.client.graph.data.HostAddress

import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.syntax._

object NebulaSessionClientDefault {

  def make(sessionPoolConfig: NebulaSessionPoolConfig): NebulaSessionClient[SyncFuture] = {
    val sessionPool = new SessionPool(
      new SessionPoolConfig(
        sessionPoolConfig.address.map(d => new HostAddress(d.host, d.port)).asJava,
        sessionPoolConfig.spaceName,
        sessionPoolConfig.auth.username,
        sessionPoolConfig.auth.password
      ).setMaxSessionSize(sessionPoolConfig.maxSessionSize)
        .setMinSessionSize(sessionPoolConfig.minSessionSize)
        .setRetryTimes(sessionPoolConfig.retryTimes)
        .setWaitTime(sessionPoolConfig.waitTimeMills)
        .setIntervalTime(sessionPoolConfig.intervalTimeMills)
        .setTimeout(sessionPoolConfig.timeoutMills)
        .setCleanTime(sessionPoolConfig.cleanTimeSeconds)
        .setReconnect(sessionPoolConfig.reconnect)
        .setHealthCheckTime(sessionPoolConfig.healthCheckTimeSeconds)
        .setUseHttp2(sessionPoolConfig.useHttp2)
    )

    new NebulaSessionClientDefault(sessionPool)
  }
}

final class NebulaSessionClientDefault(underlying: SessionPool) extends NebulaSessionClient[SyncFuture] {

  override def execute(stmt: Stmt): SyncFuture[stmt.T] =
    Future.successful {
      stmt match {
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
      }
    }

  override def idleSessionNum: SyncFuture[Int] = Future.successful(underlying.getIdleSessionNums)

  override def sessionNum: SyncFuture[Int] = Future.successful(underlying.getSessionNums)

  override def isActive: SyncFuture[Boolean] = Future.successful(underlying.isActive)

  override def isClosed: SyncFuture[Boolean] = Future.successful(underlying.isClosed)

  override def close(): SyncFuture[Unit] = Future.successful(underlying.close())

}
