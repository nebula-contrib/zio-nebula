package zio.nebula.meta

import scala.jdk.CollectionConverters._

import zio._
import zio.nebula.{ NebulaHostAddress, NebulaMetaConfig }

import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.meta.MetaManager
import com.vesoft.nebula.meta._

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/30
 */
trait NebulaMetaClient {

  def close(): Task[Unit]

  def getSpaceId(spaceName: String): Task[Int]

  def getSpace(spaceName: String): Task[SpaceItem]

  def getTagId(spaceName: String, tagName: String): Task[Int]

  def getTag(spaceName: String, tagName: String): Task[TagItem]

  def getEdgeType(spaceName: String, edgeName: String): Task[Int]

  def getLeader(spaceName: String, part: Int): Task[NebulaHostAddress]

  def getSpaceParts(spaceName: String): Task[List[Int]]

  def getPartsAlloc(spaceName: String): Task[Map[Int, List[NebulaHostAddress]]]

  def listHosts: Task[Set[NebulaHostAddress]]
}

object NebulaMetaClient {

  lazy val layer: ZLayer[NebulaMetaConfig & Scope, Nothing, NebulaMetaClient] =
    ZLayer.fromZIO {
      for {
        config <- ZIO.serviceWith[NebulaMetaConfig](_.underlying)
        manger <- ZIO.acquireRelease(
                    ZIO.attempt(
                      new NebulaMetaClientLive(
                        new MetaManager(
                          config.address.map(a => new HostAddress(a.host, a.port)).asJava,
                          config.timeoutMills,
                          config.connectionRetry,
                          config.executionRetry,
                          config.enableSSL,
                          config.casSigned.orElse(config.selfSigned).map(_.toJava).orNull
                        )
                      )
                    )
                  )(_.close().onError(e => ZIO.logErrorCause(e)).ignoreLogged)
      } yield manger

    }.orDie
}
