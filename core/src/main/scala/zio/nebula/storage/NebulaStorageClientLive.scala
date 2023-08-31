package zio.nebula.storage

import scala.jdk.CollectionConverters._

import zio._

import com.vesoft.nebula.client.storage.StorageClient

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/31
 */
private[nebula] object NebulaStorageClientLive {
  val DEFAULT_LIMIT               = 1000
  val DEFAULT_START_TIME          = 0
  val DEFAULT_END_TIME            = java.lang.Long.MAX_VALUE
  val DEFAULT_ALLOW_PART_SUCCESS  = false
  val DEFAULT_ALLOW_READ_FOLLOWER = true
}

private[nebula] final class NebulaStorageClientLive(storageClient: StorageClient) extends NebulaStorageClient {

  import NebulaStorageClientLive._

  override def connect(): Task[Boolean] = ZIO.attempt(storageClient.connect())

  override def close(): Task[Unit] = ZIO.attempt(storageClient.close())

  override def scan(scanInput: ScanInput): Task[scanInput.T] = {
    scanInput match {
      case ScanVertexInput(spaceName, part, tagName, returnCols, _limit, _between, _allowConfig) =>
        val limit       = _limit.getOrElse(DEFAULT_LIMIT)
        val between     = _between.getOrElse(Between(DEFAULT_START_TIME, DEFAULT_END_TIME))
        val allowConfig =
          _allowConfig.getOrElse(AllowConfig(DEFAULT_ALLOW_PART_SUCCESS, DEFAULT_ALLOW_READ_FOLLOWER))

        ZIO.attempt {
          (part, returnCols) match {
            case (Some(_part), Some(_returnCols)) =>
              storageClient.scanVertex(
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
            case (Some(_part), None)              =>
              storageClient.scanVertex(
                spaceName,
                _part,
                tagName,
                limit,
                between.startTime,
                between.endTime,
                allowConfig.allowPartSuccess,
                allowConfig.allowReadFromFollower
              )
            case (None, Some(_returnCols))        =>
              storageClient.scanVertex(
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
              storageClient.scanVertex(
                spaceName,
                tagName,
                limit,
                between.startTime,
                between.endTime,
                allowConfig.allowPartSuccess,
                allowConfig.allowReadFromFollower
              )
          }
        }
          .map(_.asInstanceOf[scanInput.T])

      case ScanEdgeInput(spaceName, part, edgeName, returnCols, _limit, _between, _allowConfig) =>
        val limit       = _limit.getOrElse(DEFAULT_LIMIT)
        val between     = _between.getOrElse(Between(DEFAULT_START_TIME, DEFAULT_END_TIME))
        val allowConfig =
          _allowConfig.getOrElse(AllowConfig(DEFAULT_ALLOW_PART_SUCCESS, DEFAULT_ALLOW_READ_FOLLOWER))

        ZIO.attempt {
          (part, returnCols) match {
            case (Some(_part), Some(_returnCols)) =>
              storageClient.scanEdge(
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
            case (Some(_part), None)              =>
              storageClient.scanEdge(
                spaceName,
                _part,
                edgeName,
                limit,
                between.startTime,
                between.endTime,
                allowConfig.allowPartSuccess,
                allowConfig.allowReadFromFollower
              )
            case (None, Some(_returnCols))        =>
              storageClient.scanEdge(
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
              storageClient.scanEdge(
                spaceName,
                edgeName,
                limit,
                between.startTime,
                between.endTime,
                allowConfig.allowPartSuccess,
                allowConfig.allowReadFromFollower
              )
          }
        }.map(_.asInstanceOf[scanInput.T])
    }
  }

}
