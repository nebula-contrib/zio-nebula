package nebula4scala

package zio {

  import _root_.zio._
  import nebula4scala.api._
  import nebula4scala.data._

  object syntax {

    import _root_.zio.Task
    import nebula4scala.data.input.Context

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
