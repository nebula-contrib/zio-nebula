package nebula4scala

import nebula4scala.data._
import nebula4scala.data.meta.SSLParam
import pureconfig._
import pureconfig.generic.ProductHint
import pureconfig.generic.semiauto.deriveReader

object Configs {

  implicit def dh[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  implicit val nc: ConfigReader[NebulaConfig] = deriveReader[NebulaConfig]

  implicit val nspc: ConfigReader[NebulaSessionPoolConfig] = deriveReader[NebulaSessionPoolConfig]

  implicit val na: ConfigReader[NebulaAuth] = deriveReader[NebulaAuth]

  implicit val npc: ConfigReader[NebulaPoolConfig] = deriveReader[NebulaPoolConfig]

  implicit val nha: ConfigReader[NebulaHostAddress] = deriveReader[NebulaHostAddress]

  implicit val ssp: ConfigReader[SSLParam]                = deriveReader[SSLParam]
  implicit val sspc: ConfigReader[SSLParam.CASignedSSL]   = deriveReader[SSLParam.CASignedSSL]
  implicit val ssps: ConfigReader[SSLParam.SelfSignedSSL] = deriveReader[SSLParam.SelfSignedSSL]

  implicit val nmc: ConfigReader[NebulaMetaConfig] = deriveReader[NebulaMetaConfig]

  implicit val nsc: ConfigReader[NebulaStorageConfig] = deriveReader[NebulaStorageConfig]

  private lazy val defaultGraphConfig   = ConfigSource.default.at("graph")
  private lazy val defaultMetaConfig    = ConfigSource.default.at("meta")
  private lazy val defaultStorageConfig = ConfigSource.default.at("storage")
  private lazy val defaultPoolConfig    = ConfigSource.default.at("pool")

  def metaConfig(cfg: Option[ConfigSource] = None): NebulaMetaConfig =
    NebulaMetaConfig(cfg.getOrElse(defaultMetaConfig).loadOrThrow[NebulaConfig])

  def storageConfig(cfg: Option[ConfigSource] = None): NebulaStorageConfig =
    NebulaStorageConfig(cfg.getOrElse(defaultStorageConfig).loadOrThrow[NebulaConfig])

  def sessionPoolConfig(cfg: Option[ConfigSource] = None): NebulaSessionPoolConfig =
    cfg.getOrElse(defaultGraphConfig).loadOrThrow[NebulaSessionPoolConfig]

  def poolConfig(cfg: Option[ConfigSource] = None): NebulaPoolConfig =
    cfg.getOrElse(defaultPoolConfig).loadOrThrow[NebulaPoolConfig]
}
