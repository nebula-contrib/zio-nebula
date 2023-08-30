package zio.nebula.meta

import scala.jdk.CollectionConverters._

import zio._
import zio.nebula.NebulaHostAddress

import com.vesoft.nebula.client.meta.MetaManager
import com.vesoft.nebula.meta._

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/30
 */
final class NebulaMetaManagerLive(underlying: MetaManager) extends NebulaMetaManager {

  override def close(): Task[Unit] = ZIO.attempt(underlying.close())

  override def getSpaceId(spaceName: String): Task[Int] = ZIO.attempt(underlying.getSpaceId(spaceName))

  override def getSpace(spaceName: String): Task[SpaceItem] = ZIO.attempt(underlying.getSpace(spaceName))

  override def getTagId(spaceName: String, tagName: String): Task[Int] =
    ZIO.attempt(underlying.getTagId(spaceName, tagName))

  override def getTag(spaceName: String, tagName: String): Task[TagItem] =
    ZIO.attempt(underlying.getTag(spaceName, tagName))

  override def getEdgeType(spaceName: String, edgeName: String): Task[Int] =
    ZIO.attempt(underlying.getEdgeType(spaceName, edgeName))

  override def getLeader(spaceName: String, part: Int): Task[NebulaHostAddress] =
    ZIO.attempt(underlying.getLeader(spaceName, part)).map(h => NebulaHostAddress(h.getHost, h.getPort))

  override def getSpaceParts(spaceName: String): Task[List[Int]] =
    ZIO.attempt(
      underlying
        .getSpaceParts(spaceName)
        .asScala
        .collect {
          case a if a != null => a.intValue()
        }
        .toList
    )

  override def getPartsAlloc(spaceName: String): Task[Map[Int, List[NebulaHostAddress]]] =
    ZIO.attempt(
      underlying
        .getPartsAlloc(spaceName)
        .asScala
        .collect {
          case (a, b) if a != null => a.intValue() -> b.asScala.map(h => NebulaHostAddress(h.getHost, h.getPort)).toList
        }
        .toMap
    )

  override def listHosts: Task[Set[NebulaHostAddress]] =
    ZIO.attempt(underlying.listHosts().asScala.map(h => NebulaHostAddress(h.getHost, h.getPort)).toSet)
}
