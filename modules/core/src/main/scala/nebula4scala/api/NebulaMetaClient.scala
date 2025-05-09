package nebula4scala.api

import com.vesoft.nebula.meta.{SpaceItem, TagItem}
import nebula4scala.data.NebulaHostAddress


trait NebulaMetaClient[F[_]] {

  def close(): F[Unit]

  def spaceId(spaceName: String): F[Int]

  def space(spaceName: String): F[SpaceItem]

  def tagId(spaceName: String, tagName: String): F[Int]

  def tag(spaceName: String, tagName: String): F[TagItem]

  def edgeType(spaceName: String, edgeName: String): F[Int]

  def leader(spaceName: String, part: Int): F[NebulaHostAddress]

  def spaceParts(spaceName: String): F[List[Int]]

  def partsAlloc(spaceName: String): F[Map[Int, List[NebulaHostAddress]]]

  def listHosts: F[Set[NebulaHostAddress]]
}