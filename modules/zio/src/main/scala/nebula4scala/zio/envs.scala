package nebula4scala.zio

import _root_.zio.*
import nebula4scala.api.*
import nebula4scala.data.*

object envs {

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
