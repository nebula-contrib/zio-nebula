package nebula4scala.cats.effect

import cats.effect._

import com.vesoft.nebula.meta.{ SpaceItem, TagItem }

import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.impl.NebulaMetaClientDefault
import nebula4scala.syntax._

object NebulaMetaClient {

  private final class Impl[F[_]: Async](
    underlying: NebulaMetaClient[ScalaFuture]
  ) extends NebulaMetaClient[F] {

    override def close(): F[Unit] = Async[F].fromFuture(Async[F].delay(underlying.close()))

    override def spaceId(spaceName: String): F[Int] = Async[F].fromFuture(Async[F].delay(underlying.spaceId(spaceName)))

    override def space(spaceName: String): F[SpaceItem] =
      Async[F].fromFuture(Async[F].delay(underlying.space(spaceName)))

    override def tagId(spaceName: String, tagName: String): F[Int] =
      Async[F].fromFuture(Async[F].delay(underlying.tagId(spaceName, tagName)))

    override def tag(spaceName: String, tagName: String): F[TagItem] =
      Async[F].fromFuture(Async[F].delay(underlying.tag(spaceName, tagName)))

    override def edgeType(spaceName: String, edgeName: String): F[Int] =
      Async[F].fromFuture(Async[F].delay(underlying.edgeType(spaceName, edgeName)))

    override def leader(spaceName: String, part: Int): F[NebulaHostAddress] =
      Async[F].fromFuture(Async[F].delay(underlying.leader(spaceName, part)))

    override def spaceParts(spaceName: String): F[List[Int]] =
      Async[F].fromFuture(
        Async[F].delay(
          underlying
            .spaceParts(spaceName)
        )
      )

    override def partsAlloc(spaceName: String): F[Map[Int, List[NebulaHostAddress]]] =
      Async[F].fromFuture(
        Async[F].delay(
          underlying
            .partsAlloc(spaceName)
        )
      )

    override def listHosts: F[Set[NebulaHostAddress]] =
      Async[F].fromFuture(Async[F].delay(underlying.listHosts))
  }

  def resource[F[_]: Async](config: NebulaMetaConfig): Resource[F, NebulaMetaClient[F]] =
    Resource.make(
      Async[F].blocking(new Impl(NebulaMetaClientDefault.make(config)))
    )(client => client.close())
}
