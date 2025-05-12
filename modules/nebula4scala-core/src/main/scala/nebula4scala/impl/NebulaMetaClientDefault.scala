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

  def make(config: NebulaClientConfig): NebulaMetaClient[ScalaFuture] = {
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

final class NebulaMetaClientDefault(underlying: MetaManager) extends NebulaMetaClient[ScalaFuture] {

  override def close(): ScalaFuture[Unit] = Future(underlying.close())

  override def spaceId(spaceName: String): ScalaFuture[Int] = Future(underlying.getSpaceId(spaceName))

  override def space(spaceName: String): ScalaFuture[SpaceItem] = Future(underlying.getSpace(spaceName))

  override def tagId(spaceName: String, tagName: String): ScalaFuture[Int] =
    Future(underlying.getTagId(spaceName, tagName))

  override def tag(spaceName: String, tagName: String): ScalaFuture[TagItem] =
    Future(underlying.getTag(spaceName, tagName))

  override def edgeType(spaceName: String, edgeName: String): ScalaFuture[Int] =
    Future(underlying.getEdgeType(spaceName, edgeName))

  override def leader(spaceName: String, part: Int): ScalaFuture[NebulaHostAddress] =
    Future {
      val h = underlying.getLeader(spaceName, part)
      NebulaHostAddress(h.getHost, h.getPort)
    }

  override def spaceParts(spaceName: String): ScalaFuture[List[Int]] =
    Future {
      underlying
        .getSpaceParts(spaceName)
        .asScala
        .collect {
          case a if a != null => a.intValue()
        }
        .toList
    }

  override def partsAlloc(spaceName: String): ScalaFuture[Map[Int, List[NebulaHostAddress]]] =
    Future {
      underlying
        .getPartsAlloc(spaceName)
        .asScala
        .collect {
          case (a, b) if a != null =>
            a.intValue() -> b.asScala.map(h => NebulaHostAddress(h.getHost, h.getPort)).toList
        }
        .toMap
    }

  override def listHosts: ScalaFuture[Set[NebulaHostAddress]] =
    Future { underlying.listHosts().asScala.map(h => NebulaHostAddress(h.getHost, h.getPort)).toSet }
}
