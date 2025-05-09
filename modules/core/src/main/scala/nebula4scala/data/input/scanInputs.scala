package nebula4scala.data.input

import com.vesoft.nebula.client.storage.scan.*

import nebula4scala.data.*

final case class Between(startTime: Long, endTime: Long)

final case class AllowConfig(allowPartSuccess: Boolean, allowReadFromFollower: Boolean)

sealed trait ScanInput {
  type T
}

final case class ScanVertex(
                             spaceName: String,
                             part: Option[Int],
                             tagName: String,
                             returnCols: Option[List[String]],
                             limit: Option[Int] = None,
                             between: Option[Between] = None,
                             allowConfig: Option[AllowConfig] = None
                           ) extends ScanInput {
  override type T = ScanVertexResultIterator
}

final case class ScanEdge(
                           spaceName: String,
                           part: Option[Int],
                           edgeName: String,
                           returnCols: Option[List[String]],
                           limit: Option[Int] = None,
                           between: Option[Between] = None,
                           allowConfig: Option[AllowConfig] = None
                         ) extends ScanInput {
  override type T = ScanEdgeResultIterator
}
