package nebula4scala.cats.effect

import scala.util.Try

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
    underlying: NebulaMetaClient[Try]
  ) extends NebulaMetaClient[F] {

    def close(): F[Unit] = implicitly[Effect[F]].fromTry(underlying.close())

    def spaceId(spaceName: String): F[Int] = implicitly[Effect[F]].fromTry(underlying.spaceId(spaceName))

    def space(spaceName: String): F[SpaceItem] =
      implicitly[Effect[F]].fromTry(underlying.space(spaceName))

    def tagId(spaceName: String, tagName: String): F[Int] =
      implicitly[Effect[F]].fromTry(underlying.tagId(spaceName, tagName))

    def tag(spaceName: String, tagName: String): F[TagItem] =
      implicitly[Effect[F]].fromTry(underlying.tag(spaceName, tagName))

    def edgeType(spaceName: String, edgeName: String): F[Int] =
      implicitly[Effect[F]].fromTry(underlying.edgeType(spaceName, edgeName))

    def leader(spaceName: String, part: Int): F[NebulaHostAddress] =
      implicitly[Effect[F]].fromTry(underlying.leader(spaceName, part))

    def spaceParts(spaceName: String): F[List[Int]] =
      implicitly[Effect[F]].fromTry(
        underlying
          .spaceParts(spaceName)
      )

    def partsAlloc(spaceName: String): F[Map[Int, List[NebulaHostAddress]]] =
      implicitly[Effect[F]].fromTry(
        underlying
          .partsAlloc(spaceName)
      )

    def listHosts: F[Set[NebulaHostAddress]] =
      implicitly[Effect[F]].fromTry(underlying.listHosts)
  }

  def resource[F[_]: Async](config: NebulaClientConfig): Resource[F, NebulaMetaClient[F]] =
    Resource.make(
      Async[F].blocking(new Impl(NebulaMetaClientDefault.make(config)))
    )(client => client.close())
}
