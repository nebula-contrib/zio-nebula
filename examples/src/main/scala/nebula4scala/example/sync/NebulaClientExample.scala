package nebula4scala.example.sync

import scala.concurrent.Future

import nebula4scala.Configs
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.impl.NebulaClientDefault

object NebulaClientExample {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  def main(args: Array[String]): Unit = {
    NebulaClientDefault.make
      .openSession(Configs.poolConfig(), false)
      .flatMap(
        _.execute(
          Stmt.str(
            """
              |CREATE SPACE IF NOT EXISTS test(vid_type=fixed_string(20));
              |USE test;
              |CREATE TAG IF NOT EXISTS person(name string, age int);
              |CREATE EDGE IF NOT EXISTS like(likeness double)
              |""".stripMargin
          )
        )
      )
      .flatMap(result => Future.successful(println(s"Query result: $result")))
  }

}
