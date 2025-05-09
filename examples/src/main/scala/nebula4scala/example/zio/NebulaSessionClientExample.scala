package nebula4scala.example.zio

import zio._

import nebula4scala.api.NebulaSessionClient
import nebula4scala.data._
import nebula4scala.zio._
import nebula4scala.zio.Configs._

final class NebulaSessionClientExample(sessionClient: NebulaSessionClient[Task]) {

  def execute(stmt: String): ZIO[Any, Throwable, NebulaResultSet] =
    sessionClient.execute(stmt)
}

object NebulaSessionClientExample {
  lazy val layer = ZLayer.fromFunction(new NebulaSessionClientExample(_))
}

object NebulaSessionClientMain extends ZIOAppDefault {

  override def run = (for {
    _ <- ZIO
      .serviceWithZIO[NebulaSessionClientExample](
        _.execute("""
            |INSERT VERTEX person(name, age) VALUES 
            |'Bob':('Bob', 10), 
            |'Lily':('Lily', 9),'Tom':('Tom', 10),
            |'Jerry':('Jerry', 13),
            |'John':('John', 11);""".stripMargin).flatMap(r => ZIO.logInfo(r.toString))
      )
    _ <- ZIO
      .serviceWithZIO[NebulaSessionClientExample](
        _.execute("""
            |INSERT EDGE like(likeness) VALUES
            |'Bob'->'Lily':(80.0),
            |'Bob'->'Tom':(70.0),
            |'Lily'->'Jerry':(84.0),
            |'Tom'->'Jerry':(68.3),
            |'Bob'->'John':(97.2);""".stripMargin).flatMap(r => ZIO.logInfo(r.toString))
      )
    _ <- ZIO
      .serviceWithZIO[NebulaSessionClientExample](
        _.execute("""
              |USE test;
              |MATCH (p:person) RETURN p LIMIT 4;
              |""".stripMargin)
          .flatMap(r => ZIO.logInfo(r.rows.toString()))
      )
  } yield ())
    .provide(
      Scope.default,
      NebulaSessionClientExample.layer,
      envs.SessionClientEnv
    )

}
