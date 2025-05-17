package nebula4scala.impl.future

import scala.concurrent.Future
import scala.util.Try

import com.vesoft.nebula.meta._

import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.impl.NebulaMetaClientDefault
import nebula4scala.impl.future.syntax._

object NebulaMetaClientFuture {

  def make(config: NebulaClientConfig): NebulaMetaClient[Future] = {
    new Impl(NebulaMetaClientDefault.make(config))
  }

  private final class Impl(underlying: NebulaMetaClient[Try]) extends NebulaMetaClient[Future] {

    def close(): Future[Unit] = implicitly[Effect[Future]].fromTry(underlying.close())

    def spaceId(spaceName: String): Future[Int] =
      implicitly[Effect[Future]].fromTry(underlying.spaceId(spaceName))

    def space(spaceName: String): Future[SpaceItem] =
      implicitly[Effect[Future]].fromTry(underlying.space(spaceName))

    def tagId(spaceName: String, tagName: String): Future[Int] =
      implicitly[Effect[Future]].fromTry(underlying.tagId(spaceName, tagName))

    def tag(spaceName: String, tagName: String): Future[TagItem] =
      implicitly[Effect[Future]].fromTry(underlying.tag(spaceName, tagName))

    def edgeType(spaceName: String, edgeName: String): Future[Int] =
      implicitly[Effect[Future]].fromTry(underlying.edgeType(spaceName, edgeName))

    def leader(spaceName: String, part: Int): Future[NebulaHostAddress] =
      implicitly[Effect[Future]].fromTry(underlying.leader(spaceName, part))

    def spaceParts(spaceName: String): Future[List[Int]] =
      implicitly[Effect[Future]].fromTry(underlying.spaceParts(spaceName))

    def partsAlloc(spaceName: String): Future[Map[Int, List[NebulaHostAddress]]] =
      implicitly[Effect[Future]].fromTry(underlying.partsAlloc(spaceName))

    def listHosts: Future[Set[NebulaHostAddress]] =
      implicitly[Effect[Future]].fromTry(underlying.listHosts)
  }
}
