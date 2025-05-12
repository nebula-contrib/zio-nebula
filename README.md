<img src="./logo.svg" width = "250" height = "150" alt="logo" align="right" />

---

![CI][Badge-CI] [![Nexus (Snapshots)][Badge-Snapshots]][Link-Snapshots] [![Sonatype Nexus (Releases)][Badge-Release]][Link-Release]


[Badge-CI]: https://github.com/nebula-contrib/nebula4scala/actions/workflows/scala.yml/badge.svg
[Badge-Snapshots]: https://img.shields.io/nexus/s/io.github.jxnu-liguobin/nebula4scala-core_3?server=https%3A%2F%2Foss.sonatype.org
[Link-Snapshots]: https://oss.sonatype.org/content/repositories/snapshots/io/github/jxnu-liguobin/nebula4scala-core_3/
[Link-Release]: https://index.scala-lang.org/nebula-contrib/nebula4scala/nebula4scala-core
[Badge-Release]: https://index.scala-lang.org/nebula-contrib/nebula4scala/nebula4scala-core/latest-by-scala-version.svg?platform=jvm

nebula4scala is a Scala client for NebulaGraph, designed to provide a type-safe and functional interface for interacting with NebulaGraph databases. 

It supports Scala Future, ZIO, and Cats-Effect, making it versatile for various functional programming paradigms.

## Features

- Support for Multiple Scala Versions: Compatible with Scala 2.12, Scala 2.13, and Scala 3.
- Functional Programming Support: Integrates seamlessly with Scala Future, ZIO, and Cats-Effect.
- Comprehensive Client Support: Provides full support for all NebulaGraph clients, including Session Pool, Connection Pool, Storage, and Meta.
- Type-Safe Configuration: Easily configure clients using [pureconfig](https://github.com/pureconfig/pureconfig) or [zio-config](https://github.com/zio/zio-config) for type-safe configuration management.

## Installation

**Adding Dependencies**

To use nebula4scala in your project, add the following dependencies to your `build.sbt` file. 
Choose the modules you need based on your project requirements.

**For Scala Future Support**
```sbt
// for scala Future
libraryDependencies += "io.github.jxnu-liguobin" %% "nebula4scala-core" % "<latest version>" 
```

**For ZIO Support**
```sbt
libraryDependencies += "io.github.jxnu-liguobin" %% "nebula4scala-zio" % "<latest version>"
```

**For Cats-Effect Support**
```sbt
libraryDependencies += "io.github.jxnu-liguobin" %% "nebula4scala-cats" % "<latest version>"
```

## Version Correspondence

Below is the version correspondence between cats, zio, nebula4scala, and nebula-java:

| cats-effect |  zio  | nebula4scala | nebula-java |
|:-----------:|:-----:|:------------:|:-----------:|
|    3.5.x    | 2.1.x |    0.2.0     |    3.8.4    |


## Usage

**Basic Example with Scala Future**

Here is a basic example of how to use nebula4scala with Scala Future:
[NebulaClientExample](./examples/src/main/scala/nebula4scala/example/future/NebulaClientExample.scala)

**Example with ZIO**

Here is an example of how to use nebula4scala with ZIO:
[NebulaClientExample](./examples/src/main/scala/nebula4scala/example/zio/NebulaClientExample.scala)

**Example with Cats-Effect**

Here is an example of how to use nebula4scala with Cats-Effect:
[NebulaClientExample](./examples/src/main/scala/nebula4scala/example/cats/effect/NebulaClientExample.scala)

## Configuration

The `NebulaSessionClient` configuration is defined using a HOCON (Human-Optimized Config Object Notation) file. 
Below is an example configuration for `NebulaSessionClient`:

```hocon
nebula {
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
}
```

For the entire structure, see `nebula4scala.data.NebulaClientConfig`.

Other configurations:

- `NebulaSessionClient` Configuration: `nebula4scala.data.NebulaClientConfig#graph`.
- `NebulaClient` Configuration: `nebula4scala.data.NebulaClientConfig#pool`.
- `NebulaMetaClient` Configuration: `nebula4scala.data.NebulaMetaConfig`.
- `NebulaStorageClient` Configuration: `nebula4scala.data.NebulaStorageConfig`.

For more detailed examples and additional configurations, please refer to the [examples](./examples/src/main/resources) directory in the repository.