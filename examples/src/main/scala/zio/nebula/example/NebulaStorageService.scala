package zio.nebula.example

import zio._
import zio.nebula._
import zio.nebula.storage._

final class NebulaStorageService(nebulaStorageClient: NebulaStorageClient) {

  def connect(): Task[Boolean] =
    nebulaStorageClient.connect()

  def scan(scanInput: ScanInput): Task[scanInput.T] =
    nebulaStorageClient.scan(scanInput)
}

object NebulaStorageService {
  lazy val layer = ZLayer.fromFunction(new NebulaStorageService(_))
}

object NebulaStorageServiceMain extends ZIOAppDefault {

  override def run = (for {
    connect <- ZIO
                 .serviceWithZIO[NebulaStorageService](_.connect())
    _       <- ZIO.logInfo(s"connect status: ${connect.toString}")
    scan    <- ZIO
                 .serviceWithZIO[NebulaStorageService](
                   _.scan(ScanEdgeInput("test", None, "like", None, None, None, None))
                 )
    _       <- ZIO.logInfo(s"scan result: ${scan.next().toString}")

  } yield ())
    .provide(
      Scope.default,
      NebulaConfig.storageLayer,
      NebulaStorageService.layer,
      NebulaStorageClient.layer
    )

}
