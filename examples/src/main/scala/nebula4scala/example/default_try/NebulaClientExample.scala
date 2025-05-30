package nebula4scala.example.default_try

import scala.util.{ Failure, Success, Try }

import nebula4scala.Configs
import nebula4scala.data.input._
import nebula4scala.impl.NebulaClientDefault
import nebula4scala.syntax._

object NebulaClientExample {

  def main(args: Array[String]): Unit = {
    val sessionM = NebulaClientDefault
      .make(Configs.config())
      .getSession(false)

    sessionM match {
      case Failure(exception) => exception.printStackTrace()
      case Success(session)   =>
        val res = session.execute(
          Stmt.str[Try](
            """
              |CREATE SPACE IF NOT EXISTS test(vid_type=fixed_string(20));
              |USE test;
              |CREATE TAG IF NOT EXISTS person(name string, age int);
              |CREATE EDGE IF NOT EXISTS like(likeness double)
              |""".stripMargin
          )
        )
        println(s"Query result: $res")
    }
  }

}
