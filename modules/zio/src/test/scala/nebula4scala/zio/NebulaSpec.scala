package nebula4scala.zio

import zio.*
import zio.test.*
import zio.test.TestAspect.*

import nebula4scala.api.*
import nebula4scala.data.*
import nebula4scala.data.input.*
import nebula4scala.zio.envs.*
import nebula4scala.zio.net.NebulaClient
import testcontainers.containers.ArbitraryNebulaCluster

trait NebulaSpec extends ZIOSpecDefault {

  type Nebula = Client & Storage & Meta & Scope

  val container: ArbitraryNebulaCluster = ArbitraryNebulaCluster(subnetIp = "172.30.0.0/16")

  container.start()

  override def aspects: Chunk[TestAspectAtLeastR[TestEnvironment]] =
    Chunk(TestAspect.fibers, TestAspect.timeout(180.seconds))

  override def spec =
    (specLayered @@ beforeAll(
      ZIO
        .service[NebulaPoolConfig]
        .flatMap(cfg =>
          ZIO.serviceWithZIO[NebulaClient[Task]](_.init(cfg))
            *> ZIO.serviceWithZIO[NebulaClient[Task]](
              _.openSession(cfg, false).flatMap(_.execute(Stmt.str("""
              |CREATE SPACE IF NOT EXISTS test(vid_type=fixed_string(20));
              |USE test;
              |CREATE TAG IF NOT EXISTS person(name string, age int);
              |CREATE EDGE IF NOT EXISTS like(likeness double)
              |""".stripMargin)))
            )
        )
    ) @@ sequential @@ eventually)
      .provideShared(
        Scope.default,
        ZioNebulaEnvironment.defaultMeta(container.metadHostList.head, container.metadPortList.head),
        ZioNebulaEnvironment.defaultStorage(container.metadHostList.head, container.metadPortList.head),
        ZioNebulaEnvironment.defaultClient(container.graphdHostList.head, container.graphdPortList.head)
      )

  def specLayered: Spec[Nebula, Throwable]

}
