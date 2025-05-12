package nebula4scala.zio

import zio.config._
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider

import com.typesafe.config.{ Config => TConfig, ConfigFactory }

import _root_.zio._
import nebula4scala.data._

object Configs {

  private lazy val clientConfig = deriveConfig[NebulaClientConfig]

  private lazy val defaultConfig: TConfig = ConfigFactory.load().getConfig("nebula")

  def fromConfig(c: TConfig): IO[Config.Error, NebulaClientConfig] =
    read(clientConfig.from(TypesafeConfigProvider.fromTypesafeConfig(c)))

  lazy val configLayer: ZLayer[Any, Nothing, NebulaClientConfig] =
    ZLayer.fromZIO(fromConfig(defaultConfig).orDie)
}
