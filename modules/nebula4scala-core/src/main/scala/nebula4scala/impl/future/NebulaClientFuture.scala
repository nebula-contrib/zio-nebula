package nebula4scala.impl.future

import scala.concurrent.Future
import scala.util.Try

import com.vesoft.nebula.client.graph.{ NebulaPoolConfig => _ }
import com.vesoft.nebula.client.graph.net.{ NebulaPool => Pool }

import nebula4scala.Effect
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.impl.NebulaClientDefault
import nebula4scala.impl.future.syntax._

object NebulaClientFuture {

  def make(config: NebulaClientConfig): NebulaClient[Future] = {
    new Impl(new NebulaClientDefault(config, new Pool))
  }

  private final class Impl(underlying: NebulaClient[Try]) extends NebulaClient[Future] {

    def init(): Future[Boolean] =
      implicitly[Effect[Future]].fromTry(underlying.init())

    def close(): Future[Unit] = implicitly[Effect[Future]].fromTry(underlying.close())

    def getSession(useSpace: Boolean = false): Future[NebulaSession[Future]] =
      implicitly[Effect[Future]]
        .fromTry(underlying.getSession(useSpace))
        .map(s => new NebulaSessionFuture(s))

    def activeConnNum: Future[Int] = implicitly[Effect[Future]].fromTry(underlying.activeConnNum)

    def idleConnNum: Future[Int] = implicitly[Effect[Future]].fromTry(underlying.idleConnNum)

    def waitersNum: Future[Int] = implicitly[Effect[Future]].fromTry(underlying.waitersNum)
  }
}
