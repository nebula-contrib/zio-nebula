package zio.nebula.example

import zio._
import zio.nebula._
import zio.nebula.storage._

final class NebulaStorageClientExample(nebulaStorageClient: NebulaStorageClient) {

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
    _       <- ZIO.logInfo(s"connect status: ${connect.toString}")
    scan    <- ZIO
                 .serviceWithZIO[NebulaStorageClientExample](
                   _.scan(ScanEdgeInput("test", None, "like", None, None, None, None))
                 )
    _       <- ZIO.logInfo(s"scan result: ${scan.next().toString}")

  } yield ())
    .provide(
      Scope.default,
      NebulaStorageClientExample.layer,
      StorageEnv
    )

}
