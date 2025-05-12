package nebula4scala.zio

import zio._
import zio.test._

import nebula4scala.api._
import nebula4scala.data.NebulaHostAddress
import nebula4scala.data.input._
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

  lazy val session: ZLayer[Scope, Throwable, SessionClient] = {
    // Java initializes the session in the constructor.
    ZLayer.makeSome[Scope, SessionClient](
      NebulaSessionClient.layer,
      ZLayer.succeed(
        config(
          NebulaHostAddress(container.graphdHostList.head, container.graphdPortList.head),
          NebulaHostAddress(container.metadHostList.head, container.metadPortList.head),
          NebulaHostAddress(container.storagedHostList.head, container.storagedPortList.head)
        )
      )
    )
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
            client        <- ZIO.service[NebulaSessionClient[Task]]
            sessionNum    <- client.sessionNum
            _             <- ZIO.logInfo(s"sessionNum: $sessionNum")
            res1          <- client.execute(Stmt.str[Task](insertVertexes)).flatMap(_.errorMessageM)
            _             <- ZIO.logInfo(s"execute insert vertex: $res1")
            res2          <- client.execute(Stmt.str[Task](insertEdges)).flatMap(_.errorMessageM)
            _             <- ZIO.logInfo(s"execute insert edge: $res2")
            res3          <- client.execute(Stmt.str[Task](query)).flatMap(_.rowsM)
            _             <- ZIO.logInfo(s"execute query $res3")
          } yield assertTrue(res3.size == 4)).provideSome[NebulaClient[Task]](session, Scope.default)

        }
      )
    )

}
