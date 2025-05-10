package nebula4scala.zio

import zio.{ Scope, Task, ZIO }
import zio.test._

import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.zio._

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

  lazy val session = ZioNebulaEnvironment.defaultSession(container.graphdHostList.head, container.graphdPortList.head)

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
          for {
            res1 <-
              ZIO
                .serviceWithZIO[NebulaSessionClient[Task]](_.execute(insertVertexes))
                .provide(
                  Scope.default,
                  session
                )
            _ <- ZIO.logInfo(s"exec insert vertex: ${res1.errorMessage}")
            res2 <-
              ZIO
                .serviceWithZIO[NebulaSessionClient[Task]](_.execute(insertEdges))
                .provide(
                  Scope.default,
                  session
                )
            _ <- ZIO.logInfo(s"exec insert edge: ${res2.errorMessage}")
            res3 <-
              ZIO
                .serviceWithZIO[NebulaSessionClient[Task]](_.execute(query))
                .provide(
                  Scope.default,
                  session
                )
            _ <- ZIO.logInfo(s"exec query ${res3.errorMessage}")
          } yield assertTrue(res3.rows.size == 4)
        }
      )
    )

}
