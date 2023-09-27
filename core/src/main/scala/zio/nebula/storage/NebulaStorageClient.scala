package zio.nebula.storage

import scala.jdk.CollectionConverters._

import zio._
import zio.nebula.NebulaStorageConfig

import com.vesoft.nebula.client.graph.data.HostAddress
import com.vesoft.nebula.client.storage.StorageClient

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/8/31
 */
trait NebulaStorageClient {

  def connect(): Task[Boolean]

  def close(): Task[Unit]

  def scan(scanInput: ScanInput): Task[scanInput.T]
}

object NebulaStorageClient {

  lazy val layer: ZLayer[NebulaStorageConfig & Scope, Throwable, NebulaStorageClient] =
    ZLayer.fromZIO {
      for {
        config <- ZIO.serviceWith[NebulaStorageConfig](_.underlying)
        manger <- ZIO.acquireRelease(
                    ZIO.attempt(
                      new NebulaStorageClientLive(
                        new StorageClient(
                          config.address.map(a => new HostAddress(a.host, a.port)).asJava,
                          config.timeoutMills,
                          config.connectionRetry,
                          config.executionRetry,
                          config.enableSSL,
                          config.casSigned.orElse(config.selfSigned).map(_.toJava).orNull
                        )
                      )
                    )
                  )(_.close().onError(e => ZIO.logErrorCause(e)).ignoreLogged)
      } yield manger

    }
}
