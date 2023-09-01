package zio

import zio.nebula.meta.NebulaMetaClient
import zio.nebula.net._
import zio.nebula.storage._

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/9/1
 */
package object nebula {

  type SessionClient = NebulaSessionClient
  type Client        = NebulaClient & NebulaSessionPoolConfig & NebulaPoolConfig
  type Storage       = NebulaStorageClient
  type Meta          = NebulaMetaClient

  lazy val SessionClientEnv: ZLayer[Scope, Nothing, SessionClient] = ZLayer.makeSome[Scope, SessionClient](
    NebulaSessionClient.layer,
    NebulaConfig.sessionConfigLayer
  )

  lazy val ClientEnv: ZLayer[Scope, Nothing, Client] =
    ZLayer.makeSome[Scope, Client](
      NebulaClient.layer,
      NebulaConfig.poolConfigLayer,
      NebulaConfig.sessionConfigLayer
    )

  lazy val StorageEnv: ZLayer[Scope, Nothing, Storage] = ZLayer.makeSome[Scope, Storage](
    NebulaStorageClient.layer,
    NebulaConfig.storageConfigLayer
  )

  lazy val MetaEnv: ZLayer[Scope, Nothing, Meta] = ZLayer.makeSome[Scope, Meta](
    NebulaMetaClient.layer,
    NebulaConfig.metaConfigLayer
  )
}
