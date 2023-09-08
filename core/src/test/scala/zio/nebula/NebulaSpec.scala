package zio.nebula

import zio._
import zio.nebula.net.{ NebulaClient, Stmt }
import zio.test._
import zio.test.TestAspect._

trait NebulaSpec extends ZIOSpecDefault {

  type Nebula = Client with SessionClient with Storage with Meta with Scope

  override def spec =
    (specLayered @@ beforeAll(
      ZIO.serviceWithZIO[NebulaClient](_.init())
        *> ZIO.serviceWithZIO[NebulaClient](
          _.openSession().flatMap(_.execute(Stmt.str("""
                                                       |CREATE SPACE IF NOT EXISTS test(vid_type=fixed_string(20));
                                                       |USE test;
                                                       |CREATE TAG IF NOT EXISTS person(name string, age int);
                                                       |CREATE EDGE IF NOT EXISTS like(likeness double)
                                                       |""".stripMargin)))
        ) *>
        ZIO.serviceWithZIO[NebulaSessionClient](_.init())
    ) @@ sequential)
      .provideShared(
        Scope.default,
        MetaEnv,
        StorageEnv,
        SessionClientEnv,
        ClientEnv
      )

  def specLayered: Spec[Nebula, Throwable]

}
