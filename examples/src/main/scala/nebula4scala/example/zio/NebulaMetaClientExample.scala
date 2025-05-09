package nebula4scala.example.zio

import zio.*
import com.vesoft.nebula.meta.SpaceItem
import nebula4scala.zio.*
import nebula4scala.zio.envs.*
import nebula4scala.api.*

final class NebulaMetaClientExample(metaClient: NebulaMetaClient[Task]) {

  def getSpace(spaceName: String): Task[SpaceItem] =
    metaClient.space(spaceName)
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
