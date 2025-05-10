package nebula4scala.zio

import java.util.concurrent.TimeUnit

import scala.util._

import zio._
import zio.test._

import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.zio._
import nebula4scala.zio.syntax._

object NebulaClientSpec extends NebulaSpec {

  val insertVertexes =
    """
      |INSERT VERTEX person(name, age) VALUES 
      |'Bob':('Bob', 10), 
      |'Lily':('Lily', 9),'Tom':('Tom', 10),
      |'Jerry':('Jerry', 13),
      |'John':('John', 11);""".stripMargin

  val insertEdges =
    """
      |INSERT EDGE like(likeness) VALUES
      |'Bob'->'Lily':(80.0),
      |'Bob'->'Tom':(70.0),
      |'Lily'->'Jerry':(84.0),
      |'Tom'->'Jerry':(68.3),
      |'Bob'->'John':(97.2);""".stripMargin

  val query =
    """
      |MATCH (p:person) RETURN p LIMIT 4;
      |""".stripMargin

  lazy val session = {
    // Java initializes the session in the constructor.
    def layer() = Try(Unsafe.unsafe {
      runtime ?=>
      val layer = Runtime.default.unsafe
        .run(
          ZioNebulaEnvironment
            .defaultSession(container.graphdHostList.head, container.graphdPortList.head)
            .build
            .provide(Scope.default)
            .map(_.get)
        )
        .getOrThrowFiberFailure()
      ZLayer.succeed(layer)
    })

    var ls = layer() match {
      case Failure(exception) => layer()
      case Success(value)     => Try(value)
    }
    while (ls.isFailure) {
      ls = layer()
    }
    ls.get

  }

  def specLayered: Spec[Nebula, Throwable] =
    suite("nebula suite")(
      suite("nebula meta manager")(
        test("query") {
          for {
            spaceItem <- ZIO.serviceWithZIO[NebulaMetaClient[Task]](_.space("test"))
            _         <- ZIO.logInfo(s"get space: ${spaceItem.toString}")
            spaceId   <- ZIO.serviceWithZIO[NebulaMetaClient[Task]](_.spaceId("test"))
            _         <- ZIO.logInfo(s"get space id: ${spaceId.toString}")
          } yield assertTrue(spaceItem != null && spaceId > 0)
        }
      ),
      suite("nebula storage client")(
        test("query") {
          for {
            status <- ZIO.serviceWithZIO[NebulaStorageClient[Task]](_.connect())
            _      <- ZIO.logInfo(s"connect status: ${status.toString}")
            scanResult <- ZIO.serviceWithZIO[NebulaStorageClient[Task]](
              _.scan(ScanEdge("test", None, "like", None))
            )
            _ <- ZIO.logInfo(s"scan result: $scanResult")
          } yield assertTrue(scanResult.hasNext)
        }
      ),
      suite("nebula session pool")(
        test("create and query") {
          (for {
            // Java initializes the session in the constructor.
            activeConnNum <- ZIO.serviceWithZIO[NebulaClient[Task]](_.activeConnNum)
            _             <- ZIO.logInfo(s"activeConnNum: $activeConnNum")
            sessionNum <- ZIO
              .serviceWithZIO[NebulaSessionClient[Task]](_.sessionNum)
            _ <- ZIO.logInfo(s"sessionNum: $sessionNum")
            res1 <- ZIO
              .serviceWithZIO[NebulaSessionClient[Task]](
                _.execute(Stmt.str[Task](insertVertexes)).flatMap(_.errorMessageM)
              )
            _ <- ZIO.logInfo(s"execute insert vertex: $res1")
            res2 <- ZIO
              .serviceWithZIO[NebulaSessionClient[Task]](
                _.execute(Stmt.str[Task](insertEdges)).flatMap(_.errorMessageM)
              )
            _ <- ZIO.logInfo(s"execute insert edge: $res2")
            res3 <- ZIO
              .serviceWithZIO[NebulaSessionClient[Task]](_.execute(Stmt.str[Task](query)).flatMap(_.errorMessageM))
            _ <- ZIO.logInfo(s"execute query $res3")
          } yield assertTrue(res3.length == 4)).provideSome[NebulaClient[Task]](session)

        }
      )
    )

}
