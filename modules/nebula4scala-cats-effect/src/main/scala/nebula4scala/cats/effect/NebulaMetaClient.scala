package nebula4scala.cats.effect

import cats.effect._

import com.vesoft.nebula.meta.{ SpaceItem, TagItem }

import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.cats.effect.syntax._
import nebula4scala.data._
import nebula4scala.impl.NebulaMetaClientDefault
import nebula4scala.syntax._

object NebulaMetaClient {

  private final class Impl[F[_]: Async](
    underlying: NebulaMetaClient[ScalaFuture]
  ) extends NebulaMetaClient[F] {

    def close(): F[Unit] = implicitly[Effect[F]].fromFuture(underlying.close())

    def spaceId(spaceName: String): F[Int] = implicitly[Effect[F]].fromFuture(underlying.spaceId(spaceName))

    def space(spaceName: String): F[SpaceItem] =
      implicitly[Effect[F]].fromFuture(underlying.space(spaceName))

    def tagId(spaceName: String, tagName: String): F[Int] =
      implicitly[Effect[F]].fromFuture(underlying.tagId(spaceName, tagName))

    def tag(spaceName: String, tagName: String): F[TagItem] =
      implicitly[Effect[F]].fromFuture(underlying.tag(spaceName, tagName))

    def edgeType(spaceName: String, edgeName: String): F[Int] =
      implicitly[Effect[F]].fromFuture(underlying.edgeType(spaceName, edgeName))

    def leader(spaceName: String, part: Int): F[NebulaHostAddress] =
      implicitly[Effect[F]].fromFuture(underlying.leader(spaceName, part))

    def spaceParts(spaceName: String): F[List[Int]] =
      implicitly[Effect[F]].fromFuture(
        underlying
          .spaceParts(spaceName)
      )

    def partsAlloc(spaceName: String): F[Map[Int, List[NebulaHostAddress]]] =
      implicitly[Effect[F]].fromFuture(
        underlying
          .partsAlloc(spaceName)
      )

    def listHosts: F[Set[NebulaHostAddress]] =
      implicitly[Effect[F]].fromFuture(underlying.listHosts)
  }

  def resource[F[_]: Async](config: NebulaMetaConfig): Resource[F, NebulaMetaClient[F]] =
    Resource.make(
      Async[F].blocking(new Impl(NebulaMetaClientDefault.make(config)))
    )(client => client.close())
}
