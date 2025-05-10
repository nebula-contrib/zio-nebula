package nebula4scala.example.cats

import cats._
import cats.effect._

import nebula4scala.Configs
import nebula4scala.cats._
import nebula4scala.cats.syntax._
import nebula4scala.data._
import nebula4scala.data.input._

object NebulaClientExample extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    NebulaClient
      .resource[IO]
      .use { client =>
        client
          .getSession(Configs.poolConfig(), false)
          .flatMap(
            _.execute(
              Stmt.str[IO](
                """
            |CREATE SPACE IF NOT EXISTS test(vid_type=fixed_string(20));
            |USE test;
            |CREATE TAG IF NOT EXISTS person(name string, age int);
            |CREATE EDGE IF NOT EXISTS like(likeness double)
            |""".stripMargin
              )
            )
          )
          .flatMap(result => IO(println(s"Query result: $result")))
      }
      .as(ExitCode.Success)
}
