package nebula4scala.api

import nebula4scala.data.input.ScanInput

trait NebulaStorageClient[F[_]] {

  def connect(): F[Boolean]

  def close(): F[Unit]

  def scan(scanInput: ScanInput): F[scanInput.T]
}
