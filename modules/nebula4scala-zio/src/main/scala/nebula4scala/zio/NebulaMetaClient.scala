package nebula4scala.zio

import com.vesoft.nebula.meta._

import _root_.zio._
import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.impl.NebulaMetaClientDefault
import nebula4scala.syntax._
import nebula4scala.zio.syntax._

object NebulaMetaClient {

  private final class Impl(underlying: NebulaMetaClient[ScalaFuture]) extends NebulaMetaClient[Task] {

    def close(): Task[Unit] = implicitly[Effect[Task]].fromFuture(underlying.close())

    def spaceId(spaceName: String): Task[Int] =
      implicitly[Effect[Task]].fromFuture(underlying.spaceId(spaceName))

    def space(spaceName: String): Task[SpaceItem] =
      implicitly[Effect[Task]].fromFuture(underlying.space(spaceName))

    def tagId(spaceName: String, tagName: String): Task[Int] =
      implicitly[Effect[Task]].fromFuture(underlying.tagId(spaceName, tagName))

    def tag(spaceName: String, tagName: String): Task[TagItem] =
      implicitly[Effect[Task]].fromFuture(underlying.tag(spaceName, tagName))

    def edgeType(spaceName: String, edgeName: String): Task[Int] =
      implicitly[Effect[Task]].fromFuture(underlying.edgeType(spaceName, edgeName))

    def leader(spaceName: String, part: Int): Task[NebulaHostAddress] =
      implicitly[Effect[Task]].fromFuture(underlying.leader(spaceName, part))

    def spaceParts(spaceName: String): Task[List[Int]] =
      implicitly[Effect[Task]].fromFuture(underlying.spaceParts(spaceName))

    def partsAlloc(spaceName: String): Task[Map[Int, List[NebulaHostAddress]]] =
      implicitly[Effect[Task]].fromFuture(underlying.partsAlloc(spaceName))

    def listHosts: Task[Set[NebulaHostAddress]] =
      implicitly[Effect[Task]].fromFuture(underlying.listHosts)
  }

  val layer: ZLayer[NebulaClientConfig & Scope, Throwable, NebulaMetaClient[Task]] =
    ZLayer.fromZIO {
      for {
        config <- ZIO.service[NebulaClientConfig]
        manger <- ZIO.acquireRelease(
          ZIO.attemptBlocking(
            new Impl(NebulaMetaClientDefault.make(config))
          )
        )(release => release.close().onError(e => ZIO.logErrorCause(e)).ignoreLogged)
      } yield manger

    }
}
