ZIO NebulaGraph
---

![CI][Badge-CI] [![Nexus (Snapshots)][Badge-Snapshots]][Link-Snapshots] [![Sonatype Nexus (Releases)][Badge-Release]][Link-Release]


[Badge-CI]: https://github.com/hjfruit/zio-nebula/actions/workflows/scala.yml/badge.svg
[Badge-Snapshots]: https://img.shields.io/nexus/s/io.github.jxnu-liguobin/zio-nebula_3?server=https%3A%2F%2Foss.sonatype.org
[Link-Snapshots]: https://oss.sonatype.org/content/repositories/snapshots/io/github/jxnu-liguobin/zio-nebula_3/
[Link-Release]: https://index.scala-lang.org/hjfruit/zio-nebula/zio-nebula
[Badge-Release]: https://index.scala-lang.org/hjfruit/zio-nebula/zio-nebula/latest-by-scala-version.svg?platform=jvm


[zio-nebula](https://github.com/hjfruit/zio-nebula) is a simple wrapper around [nebula-java](https://github.com/vesoft-inc/nebula-java/) for easier integration into Scala, ZIO applications.

[NebulaGraph](https://github.com/vesoft-inc/nebula) is a popular open-source graph database that can handle large volumes of data with milliseconds of latency, scale up quickly, and have the ability to perform fast graph analytics. NebulaGraph has been widely used for social media, recommendation systems, knowledge graphs, security, capital flows, AI, etc.

## Dependency

Support Scala 2 or Scala 3:
```scala
libraryDependencies += "io.github.jxnu-liguobin" %% "zio-nebula" % <latest version>
```

## Environment

- zio 2
- zio-config 4.0.0-RC16
- nebula-java 3.6.0

## Example

Usually, we use a session client, which can be conveniently used in ZIO applications like this:
```scala
import zio._
import zio.nebula._

final class NebulaSessionClientExample(nebulaSessionPool: NebulaSessionClient) {

  def execute(stmt: String): ZIO[Any, Throwable, NebulaResultSet] = {
    // Your business logic
    nebulaSessionPool.execute(stmt)
  }
}

object NebulaSessionClientExample {
  lazy val layer = ZLayer.fromFunction(new NebulaSessionClientExample(_))
}

object NebulaSessionClientMain extends ZIOAppDefault {

  override def run = (for {
    _ <- ZIO
           .serviceWithZIO[NebulaSessionClient](_.init())
    _ <- ZIO
           .serviceWithZIO[NebulaSessionClientExample](
             _.execute("""
                         |INSERT VERTEX person(name, age) VALUES 
                         |'Bob':('Bob', 10), 
                         |'Lily':('Lily', 9),'Tom':('Tom', 10),
                         |'Jerry':('Jerry', 13),
                         |'John':('John', 11);""".stripMargin).flatMap(r => ZIO.logInfo(r.toString))
           )
    _ <- ZIO
           .serviceWithZIO[NebulaSessionClientExample](
             _.execute("""
                         |INSERT EDGE like(likeness) VALUES
                         |'Bob'->'Lily':(80.0),
                         |'Bob'->'Tom':(70.0),
                         |'Lily'->'Jerry':(84.0),
                         |'Tom'->'Jerry':(68.3),
                         |'Bob'->'John':(97.2);""".stripMargin).flatMap(r => ZIO.logInfo(r.toString))
           )
    _ <- ZIO
           .serviceWithZIO[NebulaSessionClientExample](
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

See [examples](./examples/src/main/scala/zio/nebula/example/) for more clients.