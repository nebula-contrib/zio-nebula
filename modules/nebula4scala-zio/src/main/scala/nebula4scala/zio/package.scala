package nebula4scala

import scala.util.Try

import _root_.zio._
import _root_.zio.Task
import nebula4scala.api._
import nebula4scala.data._
import nebula4scala.data.input.Context
import nebula4scala.syntax._

package zio {

  object syntax {

    implicit val taskHandler: ResultSetHandler[Task] = new ResultSetHandler[Task] {

      def handle(result: Any): Task[Any] = result match {
        case set: NebulaResultSet[Try] @unchecked => ZIO.succeed(new NebulaResultSetImpl(set))
        case str: String                          => ZIO.succeed(str)
        case other => ZIO.fail(new IllegalArgumentException(s"Unexpected result type: ${other.getClass}"))
      }
    }

    implicit val zioEffect: Effect[Task] = new Effect[Task] {
      def fromTry[A](tryM: => Try[A]): Task[A]                   = ZIO.fromTry(tryM)
      def fromBlocking[A](tryMM: => Task[() => Try[A]]): Task[A] = ZIO.blocking(tryMM.flatMap(f => ZIO.fromTry(f())))
    }

    implicit val context: Context[Task] = new Context[Task] {}

    type SessionClient = NebulaSessionClient[Task]
    type Client        = NebulaClient[Task] & NebulaClientConfig
    type Storage       = NebulaStorageClient[Task]
    type Meta          = NebulaMetaClient[Task]

    // default
    lazy val SessionClientEnv: ZLayer[Scope, Throwable, SessionClient] = ZLayer.makeSome[Scope, SessionClient](
      NebulaSessionClient.layer,
      Configs.configLayer
    )

    lazy val ClientEnv: ZLayer[Scope, Throwable, Client] =
      ZLayer.makeSome[Scope, Client](
        NebulaClient.layer,
        Configs.configLayer
      )

    lazy val StorageEnv: ZLayer[Scope, Throwable, Storage] = ZLayer.makeSome[Scope, Storage](
      NebulaStorageClient.layer,
      Configs.configLayer
    )

    lazy val MetaEnv: ZLayer[Scope, Throwable, Meta] = ZLayer.makeSome[Scope, Meta](
      NebulaMetaClient.layer,
      Configs.configLayer
    )
  }
}
