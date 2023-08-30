package zio.nebula

import zio.*
import zio.config.*
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider

import com.typesafe.config.{ Config as TConfig, ConfigFactory }

final case class NebulaConfig(address: List[NebulaHostAddress], auth: NebulaAuth)

object NebulaConfig {

  private val config = deriveConfig[NebulaConfig]

  lazy val defaultConfig: TConfig = ConfigFactory.load().getConfig("graph")

  lazy val layer: ZLayer[Any, Nothing, NebulaConfig] = ZLayer.fromZIO(fromConfig(defaultConfig).orDie)

  def fromConfig(c: TConfig): IO[Config.Error, NebulaConfig] =
    read(config.from(TypesafeConfigProvider.fromTypesafeConfig(c)))
}
