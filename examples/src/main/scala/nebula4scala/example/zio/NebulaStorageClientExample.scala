package nebula4scala.example.zio

import zio._

import nebula4scala.api._
import nebula4scala.data.input._
import nebula4scala.zio.syntax._

final class NebulaStorageClientExample(nebulaStorageClient: NebulaStorageClient[Task]) {

  def connect(): Task[Boolean] =
    nebulaStorageClient.connect()

  def scan(scanInput: ScanInput): Task[scanInput.T] =
    nebulaStorageClient.scan(scanInput)
}

object NebulaStorageClientExample {
  lazy val layer = ZLayer.fromFunction(new NebulaStorageClientExample(_))
}

object NebulaStorageClientMain extends ZIOAppDefault {

  override def run = (for {
    connect <- ZIO
      .serviceWithZIO[NebulaStorageClientExample](_.connect())
    _    <- ZIO.logInfo(s"connect status: ${connect.toString}")
    scan <- ZIO
      .serviceWithZIO[NebulaStorageClientExample](
        _.scan(ScanEdge("test", None, "like", None))
      )
    _ <- ZIO.logInfo(s"scan result: ${scan.next().toString}")

  } yield ())
    .provide(
      Scope.default,
      NebulaStorageClientExample.layer,
      StorageEnv
    )

}
