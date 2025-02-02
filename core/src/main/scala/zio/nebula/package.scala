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
  type Client        = NebulaClient & NebulaPoolConfig
  type Storage       = NebulaStorageClient
  type Meta          = NebulaMetaClient

  lazy val SessionClientEnv: ZLayer[Scope, Throwable, SessionClient] = ZLayer.makeSome[Scope, SessionClient](
    NebulaSessionClient.layer,
    NebulaConfig.sessionConfigLayer
  )

  lazy val ClientEnv: ZLayer[Scope, Throwable, Client] =
    ZLayer.makeSome[Scope, Client](
      NebulaClient.layer,
      NebulaConfig.poolConfigLayer,
      NebulaConfig.sessionConfigLayer
    )

  lazy val StorageEnv: ZLayer[Scope, Throwable, Storage] = ZLayer.makeSome[Scope, Storage](
    NebulaStorageClient.layer,
    NebulaConfig.storageConfigLayer
  )

  lazy val MetaEnv: ZLayer[Scope, Throwable, Meta] = ZLayer.makeSome[Scope, Meta](
    NebulaMetaClient.layer,
    NebulaConfig.metaConfigLayer
  )
}
