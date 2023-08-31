package zio.nebula

import zio.ZIO
import zio.nebula.meta.NebulaMetaClient
import zio.nebula.net.NebulaClient
import zio.nebula.storage.{ NebulaStorageClient, ScanEdgeInput }
import zio.test._

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/28
 */
object NebulaClientSpec extends NebulaSpec {

  private def init(space: String): String =
    s"""
       |CREATE SPACE IF NOT EXISTS $space(vid_type=fixed_string(20));
       |USE $space;
       |CREATE TAG IF NOT EXISTS person(name string, age int);
       |CREATE EDGE IF NOT EXISTS like(likeness double)
       |""".stripMargin

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
      |USE test;
      |MATCH (p:person) RETURN p LIMIT 4;
      |""".stripMargin

  def specLayered: Spec[Nebula, Throwable] =
    suite("nebula suite")(
      suite("nebula session pool")(
        test("create and query") {
          for {
            res1 <- ZIO.serviceWithZIO[NebulaSessionClient](_.execute(insertVertexes))
            _    <- ZIO.logInfo(s"exec insert vertex: ${res1.getErrorMessage}")
            res2 <- ZIO.serviceWithZIO[NebulaSessionClient](_.execute(insertEdges))
            _    <- ZIO.logInfo(s"exec insert edge: ${res2.getErrorMessage}")
            res3 <- ZIO.serviceWithZIO[NebulaSessionClient](_.execute(query))
            _    <- ZIO.logInfo(s"exec query ${res3.getErrorMessage}")
          } yield assertTrue(res3.getRows.size == 4)
        }
      ),
      suite("nebula meta manager")(
        test("query") {
          for {
            initStatus <- ZIO.serviceWithZIO[NebulaClient](_.getSession.flatMap(_.execute(init("test_meta"))))
            _          <- ZIO.logInfo(s"init stmt: ${initStatus.getErrorMessage}")
            spaceItem  <- ZIO.serviceWithZIO[NebulaMetaClient](_.getSpace("test_meta"))
            _          <- ZIO.logInfo(s"get space: ${spaceItem.toString}")
            spaceId    <- ZIO.serviceWithZIO[NebulaMetaClient](_.getSpaceId("test_meta"))
            _          <- ZIO.logInfo(s"get space id: ${spaceId.toString}")
          } yield assertTrue(spaceItem != null && spaceId > 0)
        }
      ),
      suite("nebula storage client")(
        test("query") {
          for {
            initStatus <- ZIO.serviceWithZIO[NebulaClient](_.getSession.flatMap(_.execute(init("test_storage"))))
            _          <- ZIO.logInfo(s"init stmt: ${initStatus.getErrorMessage}")
            status     <- ZIO.serviceWithZIO[NebulaStorageClient](
                            _.connect()
                          )
            _          <- ZIO.logInfo(s"connect status: ${status.toString}")
            scanResult <- ZIO.serviceWithZIO[NebulaStorageClient](
                            _.scan(ScanEdgeInput("test_storage", None, "likeness", None, None, None, None))
                          )
            _          <- ZIO.logInfo(s"scan result: ${scanResult.next().toString}")
          } yield assertTrue(scanResult != null)
        }
      )
    )

}
