package zio.nebula

import java.util.Collections

import scala.jdk.CollectionConverters.*

import zio.ZIO
import zio.nebula.NebulaSessionPool
import zio.test.*

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/28
 */
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
      |USE test;
      |MATCH (p:person) RETURN p LIMIT 4;
      |""".stripMargin

  def specLayered: Spec[NebulaSessionPool, Throwable] =
    suite("nebula session pool")(
      test("create and query") {
        for {
          _ <- ZIO
                 .serviceWithZIO[NebulaSessionPool](
                   _.execute(insertVertexes).flatMap(r => ZIO.logInfo(r.toString))
                 )
          _ <- ZIO
                 .serviceWithZIO[NebulaSessionPool](
                   _.execute(insertEdges).flatMap(r => ZIO.logInfo(r.toString))
                 )
          r <- ZIO
                 .serviceWithZIO[NebulaSessionPool](_.execute(query))
        } yield assertTrue(r.getRows.size == 4)
      }
    )
}
