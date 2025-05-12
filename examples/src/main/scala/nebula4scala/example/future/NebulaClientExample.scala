package nebula4scala.example.future

import scala.concurrent.Future

import nebula4scala.Configs
import nebula4scala.data.input._
import nebula4scala.impl.NebulaClientDefault
import nebula4scala.syntax._

object NebulaClientExample {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def main(args: Array[String]): Unit = {
    for {
      session <- NebulaClientDefault
        .make(Configs.config())
        .getSession(false)
      res <- session.execute(
        Stmt.str(
          """
            |CREATE SPACE IF NOT EXISTS test(vid_type=fixed_string(20));
            |USE test;
            |CREATE TAG IF NOT EXISTS person(name string, age int);
            |CREATE EDGE IF NOT EXISTS like(likeness double)
            |""".stripMargin
        )
      )
      _ <- Future.successful(println(s"Query result: $res"))
    } yield ()
  }

}
