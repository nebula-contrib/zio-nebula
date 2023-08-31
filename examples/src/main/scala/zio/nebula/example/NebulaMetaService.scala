package zio.nebula.example

import zio._
import zio.nebula._
import zio.nebula.meta._

import com.vesoft.nebula.meta.SpaceItem

final class NebulaMetaService(nebulaMetaManager: NebulaMetaClient) {

  def getSpace(spaceName: String): Task[SpaceItem] =
    nebulaMetaManager.getSpace(spaceName)
}

object NebulaMetaService {
  lazy val layer = ZLayer.fromFunction(new NebulaMetaService(_))
}

object NebulaMetaServiceMain extends ZIOAppDefault {

  override def run =
    ZIO
      .serviceWithZIO[NebulaMetaService](_.getSpace("test"))
      .flatMap(space => ZIO.logInfo(space.toString))
      .provide(
        Scope.default,
        NebulaConfig.metaLayer,
        NebulaMetaService.layer,
        NebulaMetaClient.layer
      )

}
