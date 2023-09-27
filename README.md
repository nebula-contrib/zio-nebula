![](logo.png)

ZIO Client for NebulaGraph
---

![CI][Badge-CI] [![Nexus (Snapshots)][Badge-Snapshots]][Link-Snapshots] [![Sonatype Nexus (Releases)][Badge-Release]][Link-Release]


[Badge-CI]: https://github.com/nebula-contrib/zio-nebula/actions/workflows/scala.yml/badge.svg
[Badge-Snapshots]: https://img.shields.io/nexus/s/io.github.jxnu-liguobin/zio-nebula_3?server=https%3A%2F%2Foss.sonatype.org
[Link-Snapshots]: https://oss.sonatype.org/content/repositories/snapshots/io/github/jxnu-liguobin/zio-nebula_3/
[Link-Release]: https://index.scala-lang.org/nebula-contrib/zio-nebula/zio-nebula
[Badge-Release]: https://index.scala-lang.org/nebula-contrib/zio-nebula/zio-nebula/latest-by-scala-version.svg?platform=jvm


[zio-nebula](https://github.com/hjfruit/zio-nebula) is a simple wrapper around [nebula-java](https://github.com/vesoft-inc/nebula-java/) for easier integration into Scala, ZIO applications.

[NebulaGraph](https://github.com/vesoft-inc/nebula) is a popular open-source graph database that can handle large volumes of data with milliseconds of latency, scale up quickly, and have the ability to perform fast graph analytics. NebulaGraph has been widely used for social media, recommendation systems, knowledge graphs, security, capital flows, AI, etc.

## Dependency

Support Scala 3, Scala 2.13 and Scala 2.12:
```scala
libraryDependencies += "io.github.jxnu-liguobin" %% "zio-nebula" % <latest version>
```

## Version


There are the version correspondence between zio-nebula and nebula-java:

|  zio  | zio-nebula | nebula-java |
|:-----:|:----------:|:-----------:|
| 2.0.x |   0.0.x    |    3.6.0    |
| 2.0.x |   0.1.x    |    3.6.0    |
| 2.0.x |   0.1.1    |    3.6.1    |


## Example

Usually, we use a session client, which can be conveniently used in ZIO applications like this:
```scala
import zio._
import zio.nebula._

final class NebulaSessionClientExample(sessionClient: NebulaSessionClient) {

  def execute(stmt: String): ZIO[Any, Throwable, NebulaResultSet] = {
    // Your business logic
    sessionClient.execute(stmt)
  }
}

object NebulaSessionClientExample {
  lazy val layer = ZLayer.fromFunction(new NebulaSessionClientExample(_))
}

object NebulaSessionClientMain extends ZIOAppDefault {

  override def run = (for {
    _ <- ZIO.serviceWithZIO[NebulaSessionClient](_.init())
    _ <- ZIO.serviceWithZIO[NebulaSessionClientExample](
             _.execute("""
                         |INSERT VERTEX person(name, age) VALUES 
                         |'Bob':('Bob', 10), 
                         |'Lily':('Lily', 9),'Tom':('Tom', 10),
                         |'Jerry':('Jerry', 13),
                         |'John':('John', 11);""".stripMargin).flatMap(r => ZIO.logInfo(r.toString))
           )
    _ <- ZIO.serviceWithZIO[NebulaSessionClientExample](
             _.execute("""
                         |INSERT EDGE like(likeness) VALUES
                         |'Bob'->'Lily':(80.0),
                         |'Bob'->'Tom':(70.0),
                         |'Lily'->'Jerry':(84.0),
                         |'Tom'->'Jerry':(68.3),
                         |'Bob'->'John':(97.2);""".stripMargin).flatMap(r => ZIO.logInfo(r.toString))
           )
    _ <- ZIO.serviceWithZIO[NebulaSessionClientExample](
             _.execute("""
                         |USE test;
                         |MATCH (p:person) RETURN p LIMIT 4;
                         |""".stripMargin)
               .flatMap(r => ZIO.logInfo(r.rows.toString()))
           )
  } yield ())
    .provide(
      Scope.default,
      NebulaSessionClientExample.layer,
      SessionClientEnv
    )

}
```

## Configuration

Introduction for configuring keys:
  - key `graph` for `NebulaSessionClient`
  - key `meta` for `NebulaMetaClient`
  - key `storage` for `NebulaStorageClient`
  - key `pool` for `NebulaClient`

Sample Configuration:
```hocon
{
  graph {
    address = [
      {
        host = "127.0.0.1",
        port = 9669
      }
    ]
    auth {
      username = "root"
      password = "nebula"
    }
    spaceName = "test"
    reconnect = true
  }

  meta {
    address = [
      {
        host = "127.0.0.1",
        port = 9559
      }
    ]
    timeoutMills = 30000
    connectionRetry = 3
    executionRetry = 1
    enableSSL = false
  }

  storage {
    address = [
      {
        host = "127.0.0.1",
        port = 9559
      }
    ]
    timeoutMills = 30000
    connectionRetry = 3
    executionRetry = 1
    enableSSL = false
  }

  pool {
    timeoutMills = 60000
    enableSsl = false
    minConnsSize = 10
    maxConnsSize = 10
    intervalIdleMills = 100
    waitTimeMills = 100
  }
}
```

See [examples](./examples/src/main/scala/zio/nebula/example/) for more clients and configurations.