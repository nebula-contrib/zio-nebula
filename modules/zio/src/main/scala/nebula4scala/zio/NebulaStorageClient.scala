package nebula4scala.zio

import zio.*

import nebula4scala.*
import nebula4scala.api.*
import nebula4scala.data.*
import nebula4scala.data.input.*
import nebula4scala.impl.NebulaStorageClientDefault

object NebulaStorageClient {

  private final class Impl(underlying: NebulaStorageClient[SyncFuture]) extends NebulaStorageClient[Task] {

    override def connect(): Task[Boolean] = ZIO.fromFuture(ec => underlying.connect())

    override def close(): Task[Unit] = ZIO.fromFuture(ec => underlying.close())

    override def scan(scanInput: ScanInput): Task[scanInput.T] = {
      ZIO.fromFuture(ec => underlying.scan(scanInput))
    }

  }

  lazy val layer: ZLayer[NebulaStorageConfig & Scope, Throwable, NebulaStorageClient[Task]] =
    ZLayer.fromZIO {
      for {
        config <- ZIO.service[NebulaStorageConfig]
        manger <- ZIO.acquireRelease(
          ZIO.attempt(
            new Impl(NebulaStorageClientDefault.make(config))
          )
        )(release => release.close().onError(e => ZIO.logErrorCause(e)).ignoreLogged)
      } yield manger

    }
}
