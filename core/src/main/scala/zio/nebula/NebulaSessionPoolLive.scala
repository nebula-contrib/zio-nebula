package zio.nebula

import zio.*
import zio.nebula.*

import com.vesoft.nebula.client.graph.*

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/29
 */
final class NebulaSessionPoolLive(underlying: SessionPool) extends NebulaSessionPool {

  if (!underlying.init) {
    java.lang.System.err.print("session pool init failed.")
  }

  override def execute(stmt: String): Task[NebulaResultSet] =
    ZIO.attempt(new NebulaResultSet(underlying.execute(stmt)))

  override def getIdleSessionNums: Task[Int] = ZIO.attempt(underlying.getIdleSessionNums)

  override def getSessionNums: Task[Int] = ZIO.attempt(underlying.getSessionNums)

  override def isActive: Task[Boolean] = ZIO.attempt(underlying.isActive)

  override def isClosed: Task[Boolean] = ZIO.attempt(underlying.isClosed)

  override def close(): Task[Unit] = ZIO.attempt(underlying.close())

  override def init(): Task[Boolean] = ZIO.attempt(underlying.init())

}
