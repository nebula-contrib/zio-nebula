package zio.nebula

import zio._
import zio.nebula._
import zio.nebula.meta.NebulaMetaClient
import zio.nebula.net.NebulaClient
import zio.nebula.storage.NebulaStorageClient
import zio.test._
import zio.test.TestAspect._

trait NebulaSpec extends ZIOSpecDefault {

  type Nebula = Client & SessionClient & Storage & Meta

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
        MetaEnv,
        StorageEnv,
        SessionClientEnv,
        ClientEnv
      )

  def specLayered: Spec[Nebula, Throwable]

}
