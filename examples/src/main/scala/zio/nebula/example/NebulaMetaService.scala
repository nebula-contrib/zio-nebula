package zio.nebula.example

import zio.*
import zio.nebula.*
import zio.nebula.meta.*

import com.vesoft.nebula.meta.SpaceItem

final class NebulaMetaService(nebulaMetaManager: NebulaMetaManager) {

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
        NebulaMetaConfig.layer,
        NebulaMetaService.layer,
        NebulaMetaManager.layer
      )

}
