package nebula4scala.example.cats.effect

import cats.effect._
import cats.effect.std.Console

import nebula4scala.Configs
import nebula4scala.cats.effect.NebulaMetaClient

object NebulaMetaClientExample extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    NebulaMetaClient
      .resource[IO](Configs.metaConfig())
      .use { client => client.space("test").flatMap(item => Console[IO].print(item.toString)) }
      .as(ExitCode.Success)
}
