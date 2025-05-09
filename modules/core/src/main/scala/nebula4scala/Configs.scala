package nebula4scala

import nebula4scala.data.*
import nebula4scala.data.meta.SSLParam
import pureconfig.*
import pureconfig.generic.ProductHint
import pureconfig.generic.semiauto.deriveReader

object Configs {

  given [T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  given ConfigReader[NebulaConfig] = deriveReader[NebulaConfig]

  given ConfigReader[NebulaSessionPoolConfig] = deriveReader[NebulaSessionPoolConfig]

  given ConfigReader[NebulaAuth] = deriveReader[NebulaAuth]

  given ConfigReader[NebulaPoolConfig] = deriveReader[NebulaPoolConfig]

  given ConfigReader[NebulaHostAddress] = deriveReader[NebulaHostAddress]

  given ConfigReader[SSLParam] = deriveReader[SSLParam]

  given ConfigReader[NebulaMetaConfig] = deriveReader[NebulaMetaConfig]

  given ConfigReader[NebulaStorageConfig] = deriveReader[NebulaStorageConfig]

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
