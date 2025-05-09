package nebula4scala.example.zio

import _root_.zio._
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.zio._
import nebula4scala.zio.envs._

final class NebulaClientExample(poolConfig: NebulaPoolConfig, nebulaClient: NebulaClient[Task]) {

  def execute(stmt: String): ZIO[Scope & NebulaPoolConfig, Throwable, NebulaResultSet] =
    nebulaClient.openSession(poolConfig, false).flatMap(_.execute(Stmt.str(stmt)))
}

object NebulaClientExample {
  lazy val layer = ZLayer.fromFunction((cfg, client) => new NebulaClientExample(cfg, client))
}

object NebulaClientMain extends ZIOAppDefault {

  override def run =
    (for {
      poolConfig <- ZIO.service[NebulaPoolConfig]
      status     <- ZIO.serviceWithZIO[NebulaClient[Task]](_.init(poolConfig))
      _          <- ZIO.logInfo(status.toString)
      res <- ZIO
        .serviceWithZIO[NebulaClientExample](
          _.execute("""
              |CREATE SPACE IF NOT EXISTS test(vid_type=fixed_string(20));
              |USE test;
              |CREATE TAG IF NOT EXISTS person(name string, age int);
              |CREATE EDGE IF NOT EXISTS like(likeness double)
              |""".stripMargin).flatMap(r => ZIO.logInfo(r.toString))
        )
    } yield res)
      .provide(
        Scope.default,
        ClientEnv,
        NebulaClientExample.layer
      )

}
