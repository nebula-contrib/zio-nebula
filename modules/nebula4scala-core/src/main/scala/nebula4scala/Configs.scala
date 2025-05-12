package nebula4scala

import nebula4scala.data._
import nebula4scala.data.meta.SSLParam
import pureconfig._
import pureconfig.generic.ProductHint
import pureconfig.generic.semiauto.deriveReader

object Configs {

  implicit def dh[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  implicit val nc: ConfigReader[NebulaClientConfig] = deriveReader[NebulaClientConfig]

  implicit val nspc: ConfigReader[NebulaSessionPoolConfig] = deriveReader[NebulaSessionPoolConfig]

  implicit val na: ConfigReader[NebulaAuth] = deriveReader[NebulaAuth]

  implicit val npc: ConfigReader[NebulaPoolConfig] = deriveReader[NebulaPoolConfig]

  implicit val nha: ConfigReader[NebulaHostAddress] = deriveReader[NebulaHostAddress]

  implicit val ssp: ConfigReader[SSLParam]                = deriveReader[SSLParam]
  implicit val sspc: ConfigReader[SSLParam.CASignedSSL]   = deriveReader[SSLParam.CASignedSSL]
  implicit val ssps: ConfigReader[SSLParam.SelfSignedSSL] = deriveReader[SSLParam.SelfSignedSSL]

  implicit val nsc: ConfigReader[CommonServiceConfig] = deriveReader[CommonServiceConfig]

  private lazy val defaultNebulaConfig = ConfigSource.default.at("nebula")

  def config(cfg: Option[ConfigSource] = None): NebulaClientConfig =
    cfg.getOrElse(defaultNebulaConfig).loadOrThrow[NebulaClientConfig]

}
