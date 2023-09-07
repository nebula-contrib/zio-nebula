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
private[nebula] final class NebulaMetaClientLive(underlying: MetaManager) extends NebulaMetaClient {

  override def close(): Task[Unit] = ZIO.attempt(underlying.close())

  override def spaceId(spaceName: String): Task[Int] = ZIO.attempt(underlying.getSpaceId(spaceName))

  override def space(spaceName: String): Task[SpaceItem] = ZIO.attempt(underlying.getSpace(spaceName))

  override def tagId(spaceName: String, tagName: String): Task[Int] =
    ZIO.attempt(underlying.getTagId(spaceName, tagName))

  override def tag(spaceName: String, tagName: String): Task[TagItem] =
    ZIO.attempt(underlying.getTag(spaceName, tagName))

  override def edgeType(spaceName: String, edgeName: String): Task[Int] =
    ZIO.attempt(underlying.getEdgeType(spaceName, edgeName))

  override def leader(spaceName: String, part: Int): Task[NebulaHostAddress] =
    ZIO.attempt(underlying.getLeader(spaceName, part)).map(h => NebulaHostAddress(h.getHost, h.getPort))

  override def spaceParts(spaceName: String): Task[List[Int]] =
    ZIO.attempt(
      underlying
        .getSpaceParts(spaceName)
        .asScala
        .collect {
          case a if a != null => a.intValue()
        }
        .toList
    )

  override def partsAlloc(spaceName: String): Task[Map[Int, List[NebulaHostAddress]]] =
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
