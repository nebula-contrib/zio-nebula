package nebula4scala.impl

import scala.concurrent.Future
import scala.jdk.CollectionConverters._

import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.storage.StorageClient

import nebula4scala._
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.syntax._

object NebulaStorageClientDefault {

  def make(config: NebulaStorageConfig): NebulaStorageClient[SyncFuture] = {
    val nebulaConfig = config.underlying
    new NebulaStorageClientDefault(
      new StorageClient(
        nebulaConfig.address.map(a => new HostAddress(a.host, a.port)).asJava,
        nebulaConfig.timeoutMills,
        nebulaConfig.connectionRetry,
        nebulaConfig.executionRetry,
        nebulaConfig.enableSSL,
        nebulaConfig.casSigned.orElse(nebulaConfig.selfSigned).map(_.toJava).orNull
      )
    )
  }
}

final class NebulaStorageClientDefault(underlying: StorageClient) extends NebulaStorageClient[SyncFuture] {

  import Constant._

  override def connect(): SyncFuture[Boolean] = Future.successful(underlying.connect())

  override def close(): SyncFuture[Unit] = Future.successful(underlying.close())

  override def scan(scanInput: ScanInput): SyncFuture[scanInput.T] = Future.successful {
    scanInput match {
      case ScanVertex(spaceName, part, tagName, returnCols, _limit, _between, _allowConfig) =>
        val limit   = _limit.getOrElse(DEFAULT_LIMIT)
        val between = _between.getOrElse(Between(DEFAULT_START_TIME, DEFAULT_END_TIME))
        val allowConfig =
          _allowConfig.getOrElse(AllowConfig(DEFAULT_ALLOW_PART_SUCCESS, DEFAULT_ALLOW_READ_FOLLOWER))
        ((part, returnCols) match {
          case (Some(_part), Some(_returnCols)) =>
            underlying.scanVertex(
              spaceName,
              _part,
              tagName,
              _returnCols.asJava,
              limit,
              between.startTime,
              between.endTime,
              allowConfig.allowPartSuccess,
              allowConfig.allowReadFromFollower
            )
          case (Some(_part), None) =>
            underlying.scanVertex(
              spaceName,
              _part,
              tagName,
              limit,
              between.startTime,
              between.endTime,
              allowConfig.allowPartSuccess,
              allowConfig.allowReadFromFollower
            )
          case (None, Some(_returnCols)) =>
            underlying.scanVertex(
              spaceName,
              tagName,
              _returnCols.asJava,
              limit,
              between.startTime,
              between.endTime,
              allowConfig.allowPartSuccess,
              allowConfig.allowReadFromFollower
            )

          case (None, None) =>
            underlying.scanVertex(
              spaceName,
              tagName,
              limit,
              between.startTime,
              between.endTime,
              allowConfig.allowPartSuccess,
              allowConfig.allowReadFromFollower
            )
        }).asInstanceOf[scanInput.T]

      case ScanEdge(spaceName, part, edgeName, returnCols, _limit, _between, _allowConfig) =>
        val limit   = _limit.getOrElse(DEFAULT_LIMIT)
        val between = _between.getOrElse(Between(DEFAULT_START_TIME, DEFAULT_END_TIME))
        val allowConfig =
          _allowConfig.getOrElse(AllowConfig(DEFAULT_ALLOW_PART_SUCCESS, DEFAULT_ALLOW_READ_FOLLOWER))
        ((part, returnCols) match {
          case (Some(_part), Some(_returnCols)) =>
            underlying.scanEdge(
              spaceName,
              _part,
              edgeName,
              _returnCols.asJava,
              limit,
              between.startTime,
              between.endTime,
              allowConfig.allowPartSuccess,
              allowConfig.allowReadFromFollower
            )
          case (Some(_part), None) =>
            underlying.scanEdge(
              spaceName,
              _part,
              edgeName,
              limit,
              between.startTime,
              between.endTime,
              allowConfig.allowPartSuccess,
              allowConfig.allowReadFromFollower
            )
          case (None, Some(_returnCols)) =>
            underlying.scanEdge(
              spaceName,
              edgeName,
              _returnCols.asJava,
              limit,
              between.startTime,
              between.endTime,
              allowConfig.allowPartSuccess,
              allowConfig.allowReadFromFollower
            )

          case (None, None) =>
            underlying.scanEdge(
              spaceName,
              edgeName,
              limit,
              between.startTime,
              between.endTime,
              allowConfig.allowPartSuccess,
              allowConfig.allowReadFromFollower
            )
        }).asInstanceOf[scanInput.T]
    }
  }

}
