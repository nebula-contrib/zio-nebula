package zio.nebula

import zio._
import zio.nebula.NebulaSessionClient.sessionLayer
import zio.nebula.meta.NebulaMetaClient
import zio.nebula.net.NebulaClient
import zio.nebula.storage.NebulaStorageClient

import com.vesoft.nebula.client.graph.SessionPool

/**
 * This is the default configuration dedicated to testing.
 *
 * @author
 *   梦境迷离
 * @version 1.0,2023/9/19
 */
object ZioNebulaEnvironment {

  var defaultUser  = "root"
  var defaultPwd   = "nebula"
  var defaultSpace = "test"

  def defaultSession(host: String, port: Int): ZLayer[Scope, Throwable, SessionClient] =
    ZLayer.makeSome[Scope, SessionClient](
      NebulaSessionClient.layer,
      ZLayer.succeed(
        NebulaSessionPoolConfig(
          List(NebulaHostAddress(host, port)),
          NebulaAuth(defaultUser, defaultPwd),
          defaultSpace
        )
      )
    )

  def defaultClient(host: String, port: Int): ZLayer[Scope, Throwable, Client] =
    NebulaClient.layer ++ ZLayer.succeed(
      NebulaPoolConfig(
        timeoutMills = 60000,
        enableSsl = false,
        minConnsSize = 10,
        maxConnsSize = 10,
        intervalIdleMills = 100,
        waitTimeMills = 100,
        sslParam = None
      )
    ) ++ ZLayer.fromZIO(
      ZIO.attempt(
        NebulaSessionPoolConfig(
          List(NebulaHostAddress(host, port)),
          NebulaAuth(defaultUser, defaultPwd),
          defaultSpace
        )
      )
    )

  def defaultStorage(host: String, port: Int): ZLayer[Scope, Throwable, Storage] =
    ZLayer.makeSome[Scope, Storage](
      NebulaStorageClient.layer,
      ZLayer.succeed(
        NebulaStorageConfig(
          NebulaConfig(
            List(NebulaHostAddress(host, port)),
            30000,
            3,
            3
          )
        )
      )
    )

  def defaultMeta(host: String, port: Int): ZLayer[Scope, Throwable, Meta] =
    ZLayer.makeSome[Scope, Meta](
      NebulaMetaClient.layer,
      ZLayer.succeed(
        NebulaMetaConfig(
          NebulaConfig(
            List(NebulaHostAddress(host, port)),
            30000,
            3,
            3
          )
        )
      )
    )

}
