package nebula4scala.zio

import zio._

import nebula4scala._
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.impl.NebulaStorageClientDefault
import nebula4scala.syntax._

object NebulaStorageClient {

  private final class Impl(underlying: NebulaStorageClient[SyncFuture]) extends NebulaStorageClient[Task] {

    override def connect(): Task[Boolean] = ZIO.fromFuture(_ => underlying.connect())

    override def close(): Task[Unit] = ZIO.fromFuture(_ => underlying.close())

    override def scan(scanInput: ScanInput): Task[scanInput.T] = {
      ZIO.fromFuture(_ => underlying.scan(scanInput))
    }

  }

  val layer: ZLayer[NebulaStorageConfig & Scope, Throwable, NebulaStorageClient[Task]] =
    ZLayer.fromZIO {
      for {
        config <- ZIO.service[NebulaStorageConfig]
        manger <- ZIO.acquireRelease(
          ZIO.attemptBlocking(
            new Impl(NebulaStorageClientDefault.make(config))
          )
        )(release => release.close().onError(e => ZIO.logErrorCause(e)).ignoreLogged)
      } yield manger

    }
}
