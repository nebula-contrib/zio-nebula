package nebula4scala.zio

import zio._

import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.impl.NebulaStorageClientDefault
import nebula4scala.syntax._
import nebula4scala.zio.syntax._

object NebulaStorageClient {

  private final class Impl(underlying: NebulaStorageClient[ScalaFuture]) extends NebulaStorageClient[Task] {

    def connect(): Task[Boolean] =
      implicitly[Effect[Task]].fromFuture(underlying.connect())

    def close(): Task[Unit] = implicitly[Effect[Task]].fromFuture(underlying.close())

    def scan(scanInput: ScanInput): Task[scanInput.T] =
      implicitly[Effect[Task]].fromFuture(underlying.scan(scanInput))

  }

  val layer: ZLayer[NebulaClientConfig & Scope, Throwable, NebulaStorageClient[Task]] =
    ZLayer.fromZIO {
      for {
        config <- ZIO.service[NebulaClientConfig]
        manger <- ZIO.acquireRelease(
          ZIO.attemptBlocking(
            new Impl(NebulaStorageClientDefault.make(config))
          )
        )(release => release.close().onError(e => ZIO.logErrorCause(e)).ignoreLogged)
      } yield manger

    }
}
