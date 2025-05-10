package nebula4scala.impl

import scala.concurrent.Future
import scala.jdk.CollectionConverters._

import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.meta.MetaManager
import com.vesoft.nebula.meta._

import nebula4scala.api.NebulaMetaClient
import nebula4scala.data._
import nebula4scala.syntax._

object NebulaMetaClientDefault {

  def make(config: NebulaMetaConfig): NebulaMetaClient[SyncFuture] = {
    val nebulaConfig = config.underlying
    val manger = new MetaManager(
      nebulaConfig.address.map(a => new HostAddress(a.host, a.port)).asJava,
      nebulaConfig.timeoutMills,
      nebulaConfig.connectionRetry,
      nebulaConfig.executionRetry,
      nebulaConfig.enableSSL,
      nebulaConfig.casSigned.orElse(nebulaConfig.selfSigned).map(_.toJava).orNull
    )
    new NebulaMetaClientDefault(manger)
  }
}

final class NebulaMetaClientDefault(underlying: MetaManager) extends NebulaMetaClient[SyncFuture] {

  override def close(): SyncFuture[Unit] = Future.successful(underlying.close())

  override def spaceId(spaceName: String): SyncFuture[Int] = Future.successful(underlying.getSpaceId(spaceName))

  override def space(spaceName: String): SyncFuture[SpaceItem] = Future.successful(underlying.getSpace(spaceName))

  override def tagId(spaceName: String, tagName: String): SyncFuture[Int] =
    Future.successful(underlying.getTagId(spaceName, tagName))

  override def tag(spaceName: String, tagName: String): SyncFuture[TagItem] =
    Future.successful(underlying.getTag(spaceName, tagName))

  override def edgeType(spaceName: String, edgeName: String): SyncFuture[Int] =
    Future.successful(underlying.getEdgeType(spaceName, edgeName))

  override def leader(spaceName: String, part: Int): SyncFuture[NebulaHostAddress] =
    Future.successful {
      val h = underlying.getLeader(spaceName, part)
      NebulaHostAddress(h.getHost, h.getPort)
    }

  override def spaceParts(spaceName: String): SyncFuture[List[Int]] =
    Future.successful {
      underlying
        .getSpaceParts(spaceName)
        .asScala
        .collect {
          case a if a != null => a.intValue()
        }
        .toList
    }

  override def partsAlloc(spaceName: String): SyncFuture[Map[Int, List[NebulaHostAddress]]] =
    Future.successful {
      underlying
        .getPartsAlloc(spaceName)
        .asScala
        .collect {
          case (a, b) if a != null =>
            a.intValue() -> b.asScala.map(h => NebulaHostAddress(h.getHost, h.getPort)).toList
        }
        .toMap
    }

  override def listHosts: SyncFuture[Set[NebulaHostAddress]] =
    Future.successful { underlying.listHosts().asScala.map(h => NebulaHostAddress(h.getHost, h.getPort)).toSet }
}
