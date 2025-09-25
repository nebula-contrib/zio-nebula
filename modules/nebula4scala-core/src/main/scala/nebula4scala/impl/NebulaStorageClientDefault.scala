package nebula4scala.impl

import scala.jdk.CollectionConverters._
import scala.util.Try

import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.storage.StorageClient

import nebula4scala._
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._

object NebulaStorageClientDefault {

  def make(config: NebulaClientConfig): NebulaStorageClient[Try] = {
    new NebulaStorageClientDefault(
      new StorageClient(
        config.storage.address.map(a => new HostAddress(a.host, a.port)).asJava,
        config.storage.timeoutMills,
        config.storage.connectionRetry,
        config.storage.executionRetry,
        config.storage.enableSSL,
        config.storage.casSigned.orElse(config.storage.selfSigned).map(_.toJava).orNull
      )
    )
  }
}

final class NebulaStorageClientDefault(underlying: StorageClient) extends NebulaStorageClient[Try] {

  import Constant._

  override def connect(): Try[Boolean] = Try(underlying.connect())

  override def close(): Try[Unit] = Try(underlying.close())

  override def scan(scanInput: ScanInput): Try[scanInput.T] = Try {
    scanInput match {
      case ScanVertex(spaceName, part, tagName, returnCols, _limit, _between, _allowConfig) =>
        val limit       = _limit.getOrElse(DEFAULT_LIMIT)
        val between     = _between.getOrElse(Between(DEFAULT_START_TIME, DEFAULT_END_TIME))
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
        val limit       = _limit.getOrElse(DEFAULT_LIMIT)
        val between     = _between.getOrElse(Between(DEFAULT_START_TIME, DEFAULT_END_TIME))
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
