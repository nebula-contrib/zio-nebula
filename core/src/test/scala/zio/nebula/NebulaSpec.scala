package zio.nebula

import zio._
import zio.nebula.meta.NebulaMetaClient
import zio.nebula.net.NebulaClient
import zio.nebula.storage.NebulaStorageClient
import zio.test._
import zio.test.TestAspect._

trait NebulaSpec extends ZIOSpecDefault {

  type Nebula = NebulaSessionClient
    with NebulaMetaClient
    with NebulaStorageClient
    with NebulaClient
    with NebulaSessionPoolConfig
    with Scope

  override def spec =
    (specLayered @@ beforeAll(
      ZIO.serviceWithZIO[NebulaClient](_.init())
        *> ZIO.serviceWithZIO[NebulaClient](
          _.getSession.flatMap(_.execute("CREATE SPACE IF NOT EXISTS test(vid_type=fixed_string(20));"))
        ) *>
        ZIO.serviceWithZIO[NebulaSessionClient](_.init())
    ) @@ sequential)
      .provideShared(
        Scope.default,
        NebulaClient.layer,
        NebulaSessionClient.layer,
        NebulaMetaClient.layer,
        NebulaStorageClient.layer,
        NebulaConfig.sessionConfigLayer,
        NebulaConfig.metaConfigLayer,
        NebulaConfig.storageConfigLayer,
        NebulaConfig.poolConfigLayer
      )

  def specLayered: Spec[Nebula, Throwable]

}
