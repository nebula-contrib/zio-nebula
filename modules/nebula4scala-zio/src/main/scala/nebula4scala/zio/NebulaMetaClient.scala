package nebula4scala.zio

import com.vesoft.nebula.meta._

import _root_.zio._
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.impl.NebulaMetaClientDefault
import nebula4scala.syntax._

object NebulaMetaClient {

  private final class Impl(underlying: NebulaMetaClient[SyncFuture]) extends NebulaMetaClient[Task] {

    override def close(): Task[Unit] = ZIO.fromFuture(ec => underlying.close())

    override def spaceId(spaceName: String): Task[Int] = ZIO.fromFuture(ec => underlying.spaceId(spaceName))

    override def space(spaceName: String): Task[SpaceItem] = ZIO.fromFuture(ec => underlying.space(spaceName))

    override def tagId(spaceName: String, tagName: String): Task[Int] =
      ZIO.fromFuture(ec => underlying.tagId(spaceName, tagName))

    override def tag(spaceName: String, tagName: String): Task[TagItem] =
      ZIO.fromFuture(ec => underlying.tag(spaceName, tagName))

    override def edgeType(spaceName: String, edgeName: String): Task[Int] =
      ZIO.fromFuture(ec => underlying.edgeType(spaceName, edgeName))

    override def leader(spaceName: String, part: Int): Task[NebulaHostAddress] =
      ZIO.fromFuture(ec => underlying.leader(spaceName, part))

    override def spaceParts(spaceName: String): Task[List[Int]] =
      ZIO.fromFuture(ec =>
        underlying
          .spaceParts(spaceName)
      )

    override def partsAlloc(spaceName: String): Task[Map[Int, List[NebulaHostAddress]]] =
      ZIO.fromFuture(ec =>
        underlying
          .partsAlloc(spaceName)
      )

    override def listHosts: Task[Set[NebulaHostAddress]] =
      ZIO.fromFuture(ec => underlying.listHosts)
  }

  val layer: ZLayer[NebulaMetaConfig & Scope, Throwable, NebulaMetaClient[Task]] =
    ZLayer.fromZIO {
      for {
        config <- ZIO.service[NebulaMetaConfig]
        manger <- ZIO.acquireRelease(
          ZIO.attemptBlocking(
            new Impl(NebulaMetaClientDefault.make(config))
          )
        )(release => release.close().onError(e => ZIO.logErrorCause(e)).ignoreLogged)
      } yield manger

    }
}
