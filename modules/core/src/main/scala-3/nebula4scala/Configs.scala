package nebula4scala

import nebula4scala.data._
import nebula4scala.data.meta.SSLParam
import pureconfig._
import pureconfig.generic.ProductHint
import pureconfig.generic.semiauto.deriveReader

object Configs {

  implicit def dh[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  implicit def nc: ConfigReader[NebulaConfig] = deriveReader[NebulaConfig]

  implicit def nspc: ConfigReader[NebulaSessionPoolConfig] = deriveReader[NebulaSessionPoolConfig]

  implicit def na: ConfigReader[NebulaAuth] = deriveReader[NebulaAuth]

  implicit def npc: ConfigReader[NebulaPoolConfig] = deriveReader[NebulaPoolConfig]

  implicit def nha: ConfigReader[NebulaHostAddress] = deriveReader[NebulaHostAddress]

  implicit def ssp: ConfigReader[SSLParam] = deriveReader[SSLParam]

  implicit def nmc: ConfigReader[NebulaMetaConfig] = deriveReader[NebulaMetaConfig]

  implicit def nsc: ConfigReader[NebulaStorageConfig] = deriveReader[NebulaStorageConfig]

  private lazy val defaultGraphConfig   = ConfigSource.default.at("graph")
  private lazy val defaultMetaConfig    = ConfigSource.default.at("meta")
  private lazy val defaultStorageConfig = ConfigSource.default.at("storage")
  private lazy val defaultPoolConfig    = ConfigSource.default.at("pool")

  def metaConfig(cfg: Option[ConfigSource] = None): NebulaConfig =
    cfg.getOrElse(defaultMetaConfig).loadOrThrow[NebulaConfig]

  def storageConfig(cfg: Option[ConfigSource] = None): NebulaConfig =
    cfg.getOrElse(defaultStorageConfig).loadOrThrow[NebulaConfig]

  def sessionPoolConfig(cfg: Option[ConfigSource] = None): NebulaSessionPoolConfig =
    cfg.getOrElse(defaultGraphConfig).loadOrThrow[NebulaSessionPoolConfig]

  def poolConfig(cfg: Option[ConfigSource] = None): NebulaPoolConfig =
    cfg.getOrElse(defaultPoolConfig).loadOrThrow[NebulaPoolConfig]
}
