package nebula4scala.example.cats.effect

import cats.effect._
import cats.effect.std.Console

import nebula4scala.Configs
import nebula4scala.cats.effect._
import nebula4scala.data.input.ScanEdge

object NebulaStorageClientExample extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    NebulaStorageClient
      .resource[IO](Configs.storageConfig())
      .use { client =>
        for {
          status <- client.connect()
          _      <- Console[IO].print(status)
          edge   <- client.scan(ScanEdge("test", None, "like", None))
          _      <- Console[IO].print(edge.hasNext)
        } yield ()
      }
      .as(ExitCode.Success)
}
