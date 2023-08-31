package zio.nebula.example

import zio._
import zio.nebula._
import zio.nebula.net.NebulaClient

final class NebulaClientExample(nebulaPool: NebulaClient) {

  def execute(stmt: String): ZIO[Scope with NebulaSessionPoolConfig, Throwable, NebulaResultSet] =
    nebulaPool.getSession.flatMap(_.execute(stmt))
}

object NebulaClientExample {
  lazy val layer = ZLayer.fromFunction(new NebulaClientExample(_))
}

object NebulaClientMain extends ZIOAppDefault {

  override def run =
    (for {
      status <- ZIO.serviceWithZIO[NebulaClient](_.init())
      _      <- ZIO.logInfo(status.toString)
      res    <- ZIO
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
        NebulaClient.layer,
        NebulaClientExample.layer,
        NebulaConfig.sessionConfigLayer,
        NebulaConfig.poolConfigLayer
      )

}
