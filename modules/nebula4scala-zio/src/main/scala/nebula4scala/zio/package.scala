package nebula4scala

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
        case set: NebulaResultSet[ScalaFuture] @unchecked => ZIO.succeed(new NebulaResultSetImpl(set))
        case str: String                                  => ZIO.succeed(str)
        case other => ZIO.fail(new IllegalArgumentException(s"Unexpected result type: ${other.getClass}"))
      }
    }

    implicit val zioEffect: Effect[Task] = new Effect[Task] {
      def fromFuture[A](future: => ScalaFuture[A]): Task[A]       = ZIO.fromFuture(_ => future)
      def fromEffect[A](future: => Task[ScalaFuture[A]]): Task[A] = future.flatMap(f => ZIO.fromFuture(_ => f))
    }

    implicit val context: Context[Task] = new Context[Task] {}

    type SessionClient = NebulaSessionClient[Task]
    type Client        = NebulaClient[Task] & NebulaPoolConfig
    type Storage       = NebulaStorageClient[Task]
    type Meta          = NebulaMetaClient[Task]

    lazy val SessionClientEnv: ZLayer[Scope, Throwable, SessionClient] = ZLayer.makeSome[Scope, SessionClient](
      NebulaSessionClient.layer,
      Configs.sessionConfigLayer
    )

    lazy val ClientEnv: ZLayer[Scope, Throwable, Client] =
      ZLayer.makeSome[Scope, Client](
        NebulaClient.layer,
        Configs.poolConfigLayer
      )

    lazy val StorageEnv: ZLayer[Scope, Throwable, Storage] = ZLayer.makeSome[Scope, Storage](
      NebulaStorageClient.layer,
      Configs.storageConfigLayer
    )

    lazy val MetaEnv: ZLayer[Scope, Throwable, Meta] = ZLayer.makeSome[Scope, Meta](
      NebulaMetaClient.layer,
      Configs.metaConfigLayer
    )
  }
}
