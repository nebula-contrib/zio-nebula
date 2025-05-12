package nebula4scala.zio

import zio._
import zio.test._
import zio.test.TestAspect._

import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.zio.syntax._
import testcontainers.containers.ArbitraryNebulaCluster

trait NebulaSpec extends ZIOSpecDefault {

  var defaultUser  = "root"
  var defaultPwd   = "nebula"
  var defaultSpace = "test"

  type Nebula = Client & Storage & Meta

  val container: ArbitraryNebulaCluster = ArbitraryNebulaCluster(subnetIp = "172.30.0.0/16", version = "v3.8.0")

  container.start()

  override def aspects: Chunk[TestAspectAtLeastR[TestEnvironment]] =
    Chunk(TestAspect.fibers, TestAspect.timeout(180.seconds))

  def config(graph: NebulaHostAddress, meta: NebulaHostAddress, storage: NebulaHostAddress): NebulaClientConfig = {
    NebulaClientConfig(
      graph = NebulaSessionPoolConfig(
        List(graph),
        NebulaAuth(defaultUser, defaultPwd),
        defaultSpace,
        pool = NebulaPoolConfig(
          timeoutMills = 60000,
          enableSsl = false,
          minConnsSize = 10,
          maxConnsSize = 10,
          intervalIdleMills = 100,
          waitTimeMills = 100,
          sslParam = None
        )
      ),
      meta = CommonServiceConfig(
        List(meta),
        30000,
        3,
        3
      ),
      storage = CommonServiceConfig(
        List(storage),
        30000,
        3,
        3
      )
    )
  }

  override def spec =
    (specLayered @@ beforeAll(
      for {
        _       <- ZIO.serviceWithZIO[NebulaClient[Task]](_.init())
        client  <- ZIO.service[NebulaClient[Task]]
        session <- client.getSession(false)
        res <- session.execute(Stmt.str[Task]("""
                                                  |CREATE SPACE IF NOT EXISTS test(vid_type=fixed_string(20));
                                                  |USE test;
                                                  |CREATE TAG IF NOT EXISTS person(name string, age int);
                                                  |CREATE EDGE IF NOT EXISTS like(likeness double)
                                                  |""".stripMargin))
        _ <- ZIO.logInfo(f"init nebula: ${res}")
        _ <- ZIO.attemptBlocking(Thread.sleep(30000))
      } yield ()
    ) @@ sequential @@ eventually)
      .provideShared(
        Scope.default,
        NebulaClient.layer,
        NebulaStorageClient.layer,
        NebulaMetaClient.layer,
        ZLayer.succeed(
          config(
            NebulaHostAddress(
              container.graphdHostList.head,
              container.graphdPortList.head
            ),
            NebulaHostAddress(
              container.metadHostList.head,
              container.metadPortList.head
            ),
            NebulaHostAddress( // storage address == meta address
              container.metadHostList.head,
              container.metadPortList.head
            )
          )
        )
      )

  def specLayered: Spec[Nebula, Throwable]

}
