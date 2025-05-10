package nebula4scala.zio

import _root_.zio._
import nebula4scala.data._
import nebula4scala.zio.syntax._

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
        sslParam = None,
        address = List(NebulaHostAddress(host, port)),
        auth = NebulaAuth(defaultUser, defaultPwd),
        spaceName = Some(defaultSpace),
        reconnect = true
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
