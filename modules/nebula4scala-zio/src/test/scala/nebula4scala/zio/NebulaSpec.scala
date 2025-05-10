package nebula4scala.zio

import scala.util._

import zio._
import zio.test._
import zio.test.TestAspect._

import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.zio.syntax._
import testcontainers.containers.ArbitraryNebulaCluster

trait NebulaSpec extends ZIOSpecDefault {

  type Nebula = Client & Storage & Meta

  val container: ArbitraryNebulaCluster = ArbitraryNebulaCluster(subnetIp = "172.30.0.0/16", version = "v3.8.0")

  container.start()

  override def aspects: Chunk[TestAspectAtLeastR[TestEnvironment]] =
    Chunk(TestAspect.fibers, TestAspect.timeout(300.seconds))

  override def spec =
    (specLayered @@ beforeAll(
      ZIO
        .service[NebulaPoolConfig]
        .flatMap(cfg =>
          for {
            _       <- ZIO.serviceWithZIO[NebulaClient[Task]](_.init(cfg))
            client  <- ZIO.service[NebulaClient[Task]]
            session <- client.getSession(cfg, false)
            res <- session.execute(Stmt.str[Task]("""
                                                  |CREATE SPACE IF NOT EXISTS test(vid_type=fixed_string(20));
                                                  |USE test;
                                                  |CREATE TAG IF NOT EXISTS person(name string, age int);
                                                  |CREATE EDGE IF NOT EXISTS like(likeness double)
                                                  |""".stripMargin))
            _ <- ZIO.logInfo(f"init nebula: ${res}")
            _ <- ZIO.attemptBlocking(Thread.sleep(30000))
          } yield ()
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
