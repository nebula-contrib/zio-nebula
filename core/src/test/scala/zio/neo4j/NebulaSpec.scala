package zio.neo4j

import java.util.Properties

import zio.*
import zio.{ Scope, ZLayer }
import zio.nebula.{ NebulaConfig, NebulaSessionPool }
import zio.nebula.meta.{ NebulaMetaConfig, NebulaMetaManager }
import zio.nebula.net.NebulaPool
import zio.test.*
import zio.test.TestAspect.*
import zio.test.ZIOSpecDefault

import com.vesoft.nebula.client.graph.NebulaPoolConfig

trait NebulaSpec extends ZIOSpecDefault {

  val init =
    """
      |CREATE SPACE IF NOT EXISTS test(vid_type=fixed_string(20));
      |USE test;
      |CREATE TAG IF NOT EXISTS person(name string, age int);
      |CREATE EDGE IF NOT EXISTS like(likeness double)
      |""".stripMargin

  override def spec =
    specLayered
      .provideShared(
        Scope.default,
        NebulaSessionPool.layer,
        NebulaConfig.layer
      ) @@
      beforeAll(
        (for {
          status <- ZIO.serviceWithZIO[NebulaPool](_.init(new NebulaPoolConfig))
          _      <- ZIO.logInfo(status.toString)
          _      <- ZIO.serviceWithZIO[NebulaPool](
                      _.getSession.flatMap(_.execute(init).flatMap(r => ZIO.logInfo(r.toString)))
                    )
        } yield ()).provide(NebulaPool.layer, Scope.default, NebulaConfig.layer)
      )

  def specLayered: Spec[NebulaSessionPool, Throwable]

}
