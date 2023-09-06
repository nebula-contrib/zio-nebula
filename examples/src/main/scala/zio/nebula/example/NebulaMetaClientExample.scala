package zio.nebula.example

import zio._
import zio.nebula._
import zio.nebula.meta._

import com.vesoft.nebula.meta.SpaceItem

final class NebulaMetaClientExample(metaClient: NebulaMetaClient) {

  def getSpace(spaceName: String): Task[SpaceItem] =
    metaClient.getSpace(spaceName)
}

object NebulaMetaClientExample {
  lazy val layer = ZLayer.fromFunction(new NebulaMetaClientExample(_))
}

object NebulaMetaClientMain extends ZIOAppDefault {

  override def run =
    ZIO
      .serviceWithZIO[NebulaMetaClientExample](_.getSpace("test"))
      .flatMap(space => ZIO.logInfo(space.toString))
      .provide(
        Scope.default,
        NebulaMetaClientExample.layer,
        MetaEnv
      )

}
