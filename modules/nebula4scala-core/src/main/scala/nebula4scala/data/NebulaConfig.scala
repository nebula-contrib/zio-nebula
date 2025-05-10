package nebula4scala.data

import com.vesoft.nebula.client.graph.{ NebulaPoolConfig => PoolConfig }

import nebula4scala.data.meta.SSLParam

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
  address: List[NebulaHostAddress],
  auth: NebulaAuth,
  spaceName: Option[String],
  minConnsSize: Int = 0,
  maxConnsSize: Int = 10,
  timeoutMills: Int = 0,
  idleTimeMills: Int = 0,
  intervalIdleMills: Int = -1,
  waitTimeMills: Int = 0,
  minClusterHealthRate: Double = 1d,
  enableSsl: Boolean = false,
  sslParam: Option[SSLParam],
  reconnect: Boolean = false
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

final case class NebulaSessionPoolConfig(
  address: List[NebulaHostAddress],
  auth: NebulaAuth,
  spaceName: String,
  maxSessionSize: Int = 10,
  minSessionSize: Int = 1,
  waitTimeMills: Int = 0,
  retryTimes: Int = 3,
  timeoutMills: Int = 0,
  intervalTimeMills: Int = 0,
  healthCheckTimeSeconds: Int = 600,
  cleanTimeSeconds: Int = 3600,
  reconnect: Boolean = false,
  useHttp2: Boolean = false
)
