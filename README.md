![](logo.png)

---

![CI][Badge-CI] [![Nexus (Snapshots)][Badge-Snapshots]][Link-Snapshots] [![Sonatype Nexus (Releases)][Badge-Release]][Link-Release]


[Badge-CI]: https://github.com/nebula-contrib/zio-nebula/actions/workflows/scala.yml/badge.svg
[Badge-Snapshots]: https://img.shields.io/nexus/s/io.github.jxnu-liguobin/zio-nebula_3?server=https%3A%2F%2Foss.sonatype.org
[Link-Snapshots]: https://oss.sonatype.org/content/repositories/snapshots/io/github/jxnu-liguobin/zio-nebula_3/
[Link-Release]: https://index.scala-lang.org/nebula-contrib/zio-nebula/zio-nebula
[Badge-Release]: https://index.scala-lang.org/nebula-contrib/zio-nebula/zio-nebula/latest-by-scala-version.svg?platform=jvm

Nebula client built on top of ZIO, Cats Effect, Fs2 and official [nebula java client](https://github.com/vesoft-inc/nebula-java/)


## Introduction

- Supports all clients: Session Pool、Pool、Storage、Meta
- Support for configuring clients with typesafe config
- Other optimizations suitable for Scala pure functional
- Support Scala 3, Scala 2.13 and Scala 2.12

## Installation

In order to use this library, you need to select one based on your application and add the following line in our `build.sbt` file:
```scala
// for zio application
libraryDependencies += "io.github.jxnu-liguobin" %% "nebula4scala-zio" % <latest version>
// for cats-effect application
libraryDependencies += "io.github.jxnu-liguobin" %% "nebula4scala-cats" % <latest version>
// for scala application, synchronous wrapper by Future
libraryDependencies += "io.github.jxnu-liguobin" %% "nebula4scala-core" % <latest version> 
```

There are the version correspondence between nebula4scala and nebula-java:

| cats  |  zio  | nebula4scala | nebula-java |
|:-----:|:-----:|:------------:|:-----------:|
| 3.5.x | 2.1.x |    0.2.0     |    3.8.4    |

### Example

Usually, we use a session client, which can be conveniently used in ZIO applications like this: 
[NebulaSessionClientExample](./examples/src/main/scala/nebula4scala/example/zio/NebulaSessionClientExample.scala)

## Configuration

`NebulaSessionClient` Configuration:
> For the entire structure, see `nebula4scala.data.NebulaSessionPoolConfig`.
```hocon
graph {
  address = [
    {
      host = "127.0.0.1",
      port = 9669
    }
  ]
  auth = {
    username = "root"
    password = "nebula"
  }
  spaceName = "test"
  reconnect = true
}
```

Other configurations:

- `NebulaClient` Configuration: `nebula4scala.data.NebulaPoolConfig`.
- `NebulaMetaClient` Configuration: `nebula4scala.data.NebulaMetaConfig`.
- `NebulaStorageClient` Configuration: `nebula4scala.data.NebulaStorageConfig`.

Please see [examples](./examples/src/main/scala/nebula4scala/zio/example/) for more clients and configurations.
