package zio.nebula.storage

import com.vesoft.nebula.client.storage.scan._

final case class Between(startTime: Long, endTime: Long)
final case class AllowConfig(allowPartSuccess: Boolean, allowReadFromFollower: Boolean)

sealed trait ScanInput {
  type T
}

final case class ScanVertexInput(
  spaceName: String,
  part: Option[Int],
  tagName: String,
  returnCols: Option[List[String]],
  limit: Option[Int],
  between: Option[Between],
  allowConfig: Option[AllowConfig]
) extends ScanInput {
  override type T = ScanVertexResultIterator
}

final case class ScanEdgeInput(
  spaceName: String,
  part: Option[Int],
  edgeName: String,
  returnCols: Option[List[String]],
  limit: Option[Int],
  between: Option[Between],
  allowConfig: Option[AllowConfig]
) extends ScanInput {
  override type T = ScanEdgeResultIterator
}
