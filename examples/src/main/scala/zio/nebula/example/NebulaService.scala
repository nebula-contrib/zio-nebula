package zio.nebula.example

import zio._
import zio.nebula._
import zio.nebula.net.NebulaClient

import com.vesoft.nebula.Row

final class NebulaService(nebulaPool: NebulaClient) {

  def execute(stmt: String): ZIO[Scope with NebulaSessionConfig, Throwable, NebulaResultSet] =
    nebulaPool.getSession.flatMap(_.execute(stmt))
}

object NebulaService {
  lazy val layer = ZLayer.fromFunction(new NebulaService(_))
}

object NebulaExampleMain extends ZIOAppDefault {

  override def run =
    (for {
      status <- ZIO.serviceWithZIO[NebulaClient](_.init())
      _      <- ZIO.logInfo(status.toString)
      res    <- ZIO
                  .serviceWithZIO[NebulaService](
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
        NebulaClient.layer,
        NebulaService.layer,
        NebulaConfig.layer,
        NebulaConfig.poolLayer
      )

}
