package zio.nebula

import zio.{ ZIO, _ }
import zio.nebula.meta.NebulaMetaManager
import zio.nebula.net.NebulaPool
import zio.nebula.storage.NebulaStorageClient
import zio.test._
import zio.test.TestAspect._

trait NebulaSpec extends ZIOSpecDefault {

  type Nebula = NebulaSessionPool
    with NebulaMetaManager
    with NebulaStorageClient
    with NebulaPool
    with NebulaSessionConfig
    with Scope

  override def spec =
    (specLayered @@ beforeAll(
      ZIO.serviceWithZIO[NebulaPool](_.init())
        *> ZIO.serviceWithZIO[NebulaPool](
          _.getSession.flatMap(_.execute("CREATE SPACE IF NOT EXISTS test(vid_type=fixed_string(20));"))
        ) *>
        ZIO.serviceWithZIO[NebulaSessionPool](_.init())
    ) @@ sequential)
      .provideShared(
        Scope.default,
        NebulaPool.layer,
        NebulaSessionPool.layer,
        NebulaMetaManager.layer,
        NebulaStorageClient.layer,
        NebulaConfig.layer,
        NebulaConfig.metaLayer,
        NebulaConfig.storageLayer,
        NebulaConfig.poolLayer
      )

  def specLayered: Spec[Nebula, Throwable]

}
