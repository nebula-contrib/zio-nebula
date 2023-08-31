package zio.nebula.example

import zio._
import zio.nebula._

final class NebulaSessionPoolService(nebulaSessionPool: NebulaSessionClient) {

  def execute(stmt: String): ZIO[Any, Throwable, NebulaResultSet] =
    nebulaSessionPool.execute(stmt)
}

object NebulaSessionPoolService {
  lazy val layer = ZLayer.fromFunction(new NebulaSessionPoolService(_))
}

object NebulaSessionPoolExampleMain extends ZIOAppDefault {

  override def run = (for {
    _ <- ZIO
           .serviceWithZIO[NebulaSessionClient](_.init())
    _ <- ZIO
           .serviceWithZIO[NebulaSessionPoolService](
             _.execute("""
                         |INSERT VERTEX person(name, age) VALUES 
                         |'Bob':('Bob', 10), 
                         |'Lily':('Lily', 9),'Tom':('Tom', 10),
                         |'Jerry':('Jerry', 13),
                         |'John':('John', 11);""".stripMargin).flatMap(r => ZIO.logInfo(r.toString))
           )
    _ <- ZIO
           .serviceWithZIO[NebulaSessionPoolService](
             _.execute("""
                         |INSERT EDGE like(likeness) VALUES
                         |'Bob'->'Lily':(80.0),
                         |'Bob'->'Tom':(70.0),
                         |'Lily'->'Jerry':(84.0),
                         |'Tom'->'Jerry':(68.3),
                         |'Bob'->'John':(97.2);""".stripMargin).flatMap(r => ZIO.logInfo(r.toString))
           )
    _ <- ZIO
           .serviceWithZIO[NebulaSessionPoolService](
             _.execute("""
                         |USE test;
                         |MATCH (p:person) RETURN p LIMIT 4;
                         |""".stripMargin)
               .flatMap(r => ZIO.logInfo(r.getRows.toString()))
           )
  } yield ())
    .provide(
      Scope.default,
      NebulaSessionClient.layer,
      NebulaConfig.layer,
      NebulaSessionPoolService.layer
    )

}
