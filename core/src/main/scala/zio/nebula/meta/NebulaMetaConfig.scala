package zio.nebula.meta

import java.time.Duration

import zio._
import zio.config._
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider
import zio.nebula.NebulaHostAddress

import com.typesafe.config.{ Config => TConfig, ConfigFactory }

final case class NebulaMetaConfig(
  address: List[NebulaHostAddress],
  timeout: Duration,
  executionRetry: Int,
  connectionRetry: Int,
  enableSSL: Boolean = false,
  casSigned: Option[SSLParam] = None,
  selfSigned: Option[SSLParam] = None
)

object NebulaMetaConfig {

  private val config = deriveConfig[NebulaMetaConfig]

  lazy val defaultConfig: TConfig = ConfigFactory.load().getConfig("meta")

  lazy val layer: ZLayer[Any, Nothing, NebulaMetaConfig] = ZLayer.fromZIO(fromConfig(defaultConfig).orDie)

  def fromConfig(c: TConfig): IO[Config.Error, NebulaMetaConfig] =
    read(config.from(TypesafeConfigProvider.fromTypesafeConfig(c)))
}
