package zio.nebula.example

import zio.*
import zio.nebula.*
import zio.nebula.net.NebulaPool

import com.vesoft.nebula.Row
import com.vesoft.nebula.client.graph.NebulaPoolConfig

final class NebulaService(nebulaPool: NebulaPool) {

  def execute(stmt: String): ZIO[Scope & NebulaConfig, Throwable, NebulaResultSet] =
    nebulaPool.getSession.flatMap(_.execute(stmt))
}

object NebulaService {
  lazy val layer = ZLayer.fromFunction(new NebulaService(_))
}

object NebulaExampleMain extends ZIOAppDefault {

  override def run =
    (for {
      status <- ZIO.serviceWithZIO[NebulaPool](_.init(new NebulaPoolConfig))
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
        NebulaPool.layer,
        NebulaService.layer,
        NebulaConfig.layer
      )

}
