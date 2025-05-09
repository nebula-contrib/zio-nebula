package nebula4scala.zio

import zio.config._
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider

import com.typesafe.config.{ Config as TConfig, ConfigFactory }

import _root_.zio._
import nebula4scala.data._

object Configs {

  private lazy val config       = deriveConfig[NebulaSessionPoolConfig]
  private lazy val clientConfig = deriveConfig[NebulaConfig]
  private lazy val poolConfig   = deriveConfig[NebulaPoolConfig]

  private lazy val defaultGraphConfig: TConfig   = ConfigFactory.load().getConfig("graph")
  private lazy val defaultMetaConfig: TConfig    = ConfigFactory.load().getConfig("meta")
  private lazy val defaultStorageConfig: TConfig = ConfigFactory.load().getConfig("storage")
  private lazy val defaultPoolConfig: TConfig    = ConfigFactory.load().getConfig("pool")

  def fromMetaClientConfig(c: TConfig): IO[Config.Error, NebulaConfig] =
    read(clientConfig.from(TypesafeConfigProvider.fromTypesafeConfig(c)))

  def fromStorageClientConfig(c: TConfig): IO[Config.Error, NebulaConfig] =
    read(clientConfig.from(TypesafeConfigProvider.fromTypesafeConfig(c)))

  def fromConfig(c: TConfig): IO[Config.Error, NebulaSessionPoolConfig] =
    read(config.from(TypesafeConfigProvider.fromTypesafeConfig(c)))

  def fromPoolConfig(c: TConfig): IO[Config.Error, NebulaPoolConfig] = {
    read(poolConfig.from(TypesafeConfigProvider.fromTypesafeConfig(c)))
  }

  lazy val sessionConfigLayer: ZLayer[Any, Nothing, NebulaSessionPoolConfig] =
    ZLayer.fromZIO(fromConfig(defaultGraphConfig).orDie)

  lazy val metaConfigLayer: ZLayer[Any, Nothing, NebulaMetaConfig] =
    ZLayer.fromZIO(fromMetaClientConfig(defaultMetaConfig).orDie.map(NebulaMetaConfig.apply))

  lazy val storageConfigLayer: ZLayer[Any, Nothing, NebulaStorageConfig] =
    ZLayer.fromZIO(fromStorageClientConfig(defaultStorageConfig).orDie.map(NebulaStorageConfig.apply))

  lazy val poolConfigLayer: ZLayer[Any, Nothing, NebulaPoolConfig] =
    ZLayer.fromZIO(fromPoolConfig(defaultPoolConfig).orDie)

}
