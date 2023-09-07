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
  private val DEFAULT_LIMIT: Int                   = 1000
  private val DEFAULT_START_TIME: Long             = 0
  private val DEFAULT_END_TIME: Long               = java.lang.Long.MAX_VALUE
  private val DEFAULT_ALLOW_PART_SUCCESS: Boolean  = false
  private val DEFAULT_ALLOW_READ_FOLLOWER: Boolean = true
}

private[nebula] final class NebulaStorageClientLive(underlying: StorageClient) extends NebulaStorageClient {

  import NebulaStorageClientLive._

  override def connect(): Task[Boolean] = ZIO.attemptBlocking(underlying.connect())

  override def close(): Task[Unit] = ZIO.attempt(underlying.close())

  override def scan(scanInput: ScanInput): Task[scanInput.T] = {
    scanInput match {
      case ScanVertex(spaceName, part, tagName, returnCols, _limit, _between, _allowConfig) =>
        val limit       = _limit.getOrElse(DEFAULT_LIMIT)
        val between     = _between.getOrElse(Between(DEFAULT_START_TIME, DEFAULT_END_TIME))
        val allowConfig =
          _allowConfig.getOrElse(AllowConfig(DEFAULT_ALLOW_PART_SUCCESS, DEFAULT_ALLOW_READ_FOLLOWER))

        ZIO.attempt {
          (part, returnCols) match {
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
            case (Some(_part), None)              =>
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
            case (None, Some(_returnCols))        =>
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
          }
        }
          .map(_.asInstanceOf[scanInput.T])

      case ScanEdge(spaceName, part, edgeName, returnCols, _limit, _between, _allowConfig) =>
        val limit       = _limit.getOrElse(DEFAULT_LIMIT)
        val between     = _between.getOrElse(Between(DEFAULT_START_TIME, DEFAULT_END_TIME))
        val allowConfig =
          _allowConfig.getOrElse(AllowConfig(DEFAULT_ALLOW_PART_SUCCESS, DEFAULT_ALLOW_READ_FOLLOWER))

        ZIO.attempt {
          (part, returnCols) match {
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
            case (Some(_part), None)              =>
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
            case (None, Some(_returnCols))        =>
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
          }
        }.map(_.asInstanceOf[scanInput.T])
    }
  }

}
