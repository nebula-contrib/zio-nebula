package nebula4scala.impl.future

import scala.concurrent.Future
import scala.util.Try

import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input._
import nebula4scala.impl.NebulaStorageClientDefault
import nebula4scala.impl.future.syntax._

object NebulaStorageClientFuture {

  def make(config: NebulaClientConfig): NebulaStorageClient[Future] = {
    new Impl(NebulaStorageClientDefault.make(config))
  }

  private final class Impl(underlying: NebulaStorageClient[Try]) extends NebulaStorageClient[Future] {

    def connect(): Future[Boolean] =
      implicitly[Effect[Future]].fromTry(underlying.connect())

    def close(): Future[Unit] = implicitly[Effect[Future]].fromTry(underlying.close())

    def scan(scanInput: ScanInput): Future[scanInput.T] =
      implicitly[Effect[Future]].fromTry(underlying.scan(scanInput))

  }

}
