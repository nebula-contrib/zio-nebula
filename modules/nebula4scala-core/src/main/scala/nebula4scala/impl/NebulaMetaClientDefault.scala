package nebula4scala.impl

import scala.jdk.CollectionConverters._
import scala.util.Try

import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.meta.MetaManager
import com.vesoft.nebula.meta._

import nebula4scala.api.NebulaMetaClient
import nebula4scala.data._

object NebulaMetaClientDefault {

  def make(config: NebulaClientConfig): NebulaMetaClient[Try] = {
    val manger = new MetaManager(
      config.meta.address.map(a => new HostAddress(a.host, a.port)).asJava,
      config.meta.timeoutMills,
      config.meta.connectionRetry,
      config.meta.executionRetry,
      config.meta.enableSSL,
      config.meta.casSigned.orElse(config.meta.selfSigned).map(_.toJava).orNull
    )
    new NebulaMetaClientDefault(manger)
  }
}

final class NebulaMetaClientDefault(underlying: MetaManager) extends NebulaMetaClient[Try] {

  override def close(): Try[Unit] = Try(underlying.close())

  override def spaceId(spaceName: String): Try[Int] = Try(underlying.getSpaceId(spaceName))

  override def space(spaceName: String): Try[SpaceItem] = Try(underlying.getSpace(spaceName))

  override def tagId(spaceName: String, tagName: String): Try[Int] =
    Try(underlying.getTagId(spaceName, tagName))

  override def tag(spaceName: String, tagName: String): Try[TagItem] =
    Try(underlying.getTag(spaceName, tagName))

  override def edgeType(spaceName: String, edgeName: String): Try[Int] =
    Try(underlying.getEdgeType(spaceName, edgeName))

  override def leader(spaceName: String, part: Int): Try[NebulaHostAddress] =
    Try {
      val h = underlying.getLeader(spaceName, part)
      NebulaHostAddress(h.getHost, h.getPort)
    }

  override def spaceParts(spaceName: String): Try[List[Int]] =
    Try {
      underlying
        .getSpaceParts(spaceName)
        .asScala
        .collect {
          case a if a != null => a.intValue()
        }
        .toList
    }

  override def partsAlloc(spaceName: String): Try[Map[Int, List[NebulaHostAddress]]] =
    Try {
      underlying
        .getPartsAlloc(spaceName)
        .asScala
        .collect {
          case (a, b) if a != null =>
            a.intValue() -> b.asScala.map(h => NebulaHostAddress(h.getHost, h.getPort)).toList
        }
        .toMap
    }

  override def listHosts: Try[Set[NebulaHostAddress]] =
    Try { underlying.listHosts().asScala.map(h => NebulaHostAddress(h.getHost, h.getPort)).toSet }
}
