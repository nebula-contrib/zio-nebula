package zio.nebula

import zio.{ Config, IO, ZLayer }
import zio.config._
import zio.config.magnolia.deriveConfig
import zio.config.typesafe.TypesafeConfigProvider
import zio.nebula.meta.SSLParam

import com.typesafe.config.{ Config => TConfig, ConfigFactory }
import com.vesoft.nebula.client.graph.{ NebulaPoolConfig => PoolConfig }

final case class NebulaConfig(
  address: List[NebulaHostAddress],
  timeoutMills: Int,
  executionRetry: Int,
  connectionRetry: Int,
  enableSSL: Boolean = false,
  casSigned: Option[SSLParam] = None,
  selfSigned: Option[SSLParam] = None
)

final case class NebulaMetaConfig(
  underlying: NebulaConfig
)

final case class NebulaStorageConfig(
  underlying: NebulaConfig
)

final case class NebulaPoolConfig(
  minConnsSize: Int = 0,
  maxConnsSize: Int = 10,
  timeoutMills: Int = 0,
  idleTimeMills: Int = 0,
  intervalIdleMills: Int = -1,
  waitTimeMills: Int = 0,
  minClusterHealthRate: Double = 1d,
  enableSsl: Boolean = false,
  sslParam: Option[SSLParam]
) {

  def toJava: PoolConfig = {
    val poolConfig = new PoolConfig()
    poolConfig.setTimeout(timeoutMills)
    poolConfig.setIdleTime(idleTimeMills)
    sslParam.foreach(p => poolConfig.setSslParam(p.toJava))
    poolConfig.setWaitTime(waitTimeMills)
    poolConfig.setEnableSsl(enableSsl)
    poolConfig.setIntervalIdle(intervalIdleMills)
    poolConfig.setMaxConnSize(maxConnsSize)
    poolConfig.setMinClusterHealthRate(minClusterHealthRate)
    poolConfig.setMinConnSize(minConnsSize)
    poolConfig
  }
}

final case class NebulaSessionConfig(
  address: List[NebulaHostAddress],
  auth: NebulaAuth,
  spaceName: String,
  maxSessionSize: Int = 1,
  minSessionSize: Int = 10,
  waitTimeMills: Int = 0,
  retryTimes: Int = 3,
  timeoutMills: Int = 0,
  intervalTimeMills: Int = 0,
  healthCheckTimeSeconds: Int = 600,
  cleanTimeSeconds: Int = 3600,
  reconnect: Boolean = false
)

object NebulaConfig {
  private lazy val config       = deriveConfig[NebulaSessionConfig]
  private lazy val clientConfig = deriveConfig[NebulaConfig]
  private lazy val poolConfig   = deriveConfig[NebulaPoolConfig]

  private lazy val defaultGraphConfig: TConfig   = ConfigFactory.load().getConfig("graph")
  private lazy val defaultMetaConfig: TConfig    = ConfigFactory.load().getConfig("meta")
  private lazy val defaultStorageConfig: TConfig = ConfigFactory.load().getConfig("storage")
  private lazy val defaultPoolConfig: TConfig    = ConfigFactory.load().getConfig("pool")

  def fromClientConfig(c: TConfig): IO[Config.Error, NebulaConfig] =
    read(clientConfig.from(TypesafeConfigProvider.fromTypesafeConfig(c)))

  def fromConfig(c: TConfig): IO[Config.Error, NebulaSessionConfig] =
    read(config.from(TypesafeConfigProvider.fromTypesafeConfig(c)))

  def fromPoolConfig(c: TConfig): IO[Config.Error, NebulaPoolConfig] =
    read(poolConfig.from(TypesafeConfigProvider.fromTypesafeConfig(c)))

  lazy val layer: ZLayer[Any, Nothing, NebulaSessionConfig] = ZLayer.fromZIO(fromConfig(defaultGraphConfig).orDie)

  lazy val metaLayer: ZLayer[Any, Nothing, NebulaMetaConfig] =
    ZLayer.fromZIO(fromClientConfig(defaultMetaConfig).orDie.map(NebulaMetaConfig.apply))

  lazy val storageLayer: ZLayer[Any, Nothing, NebulaStorageConfig] =
    ZLayer.fromZIO(fromClientConfig(defaultStorageConfig).orDie.map(NebulaStorageConfig.apply))

  lazy val poolLayer: ZLayer[Any, Nothing, NebulaPoolConfig] = ZLayer.fromZIO(fromPoolConfig(defaultPoolConfig).orDie)

}
