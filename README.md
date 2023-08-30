ZIO NebulaGraph
---

![CI][Badge-CI] [![Nexus (Snapshots)][Badge-Snapshots]][Link-Snapshots] [![Sonatype Nexus (Releases)][Badge-Release]][Link-Release]


[Badge-CI]: https://github.com/hjfruit/zio-nebula/actions/workflows/scala.yml/badge.svg
[Badge-Snapshots]: https://img.shields.io/nexus/s/io.github.jxnu-liguobin/zio-nebula_3?server=https%3A%2F%2Foss.sonatype.org
[Link-Snapshots]: https://oss.sonatype.org/content/repositories/snapshots/io/github/jxnu-liguobin/zio-nebula_3/
[Link-Release]: https://oss.sonatype.org/content/repositories/public/io/github/jxnu-liguobin/zio-nebula_3/
[Badge-Release]: https://img.shields.io/nexus/r/io.github.jxnu-liguobin/zio-nebula_3?server=https%3A%2F%2Foss.sonatype.org

[NebulaGraph](https://github.com/vesoft-inc/nebula) is a popular open-source graph database that can handle large volumes of data with milliseconds of latency, scale up quickly, and have the ability to perform fast graph analytics. NebulaGraph has been widely used for social media, recommendation systems, knowledge graphs, security, capital flows, AI, etc.

## Dependency

Scala 3
```scala
libraryDependencies += "io.github.jxnu-liguobin" %% "zio-nebula" % <latest version>
```

Scala 2.13.6+ (sbt 1.5.x)
```scala
libraryDependencies += 
  ("io.github.jxnu-liguobin" %% "zio-nebula" % <latest version>).cross(CrossVersion.for2_13Use3)
```

These dependencies are required in the project classpath:
```scala
libraryDependencies ++= Seq(
  "dev.zio" %% "zio"         % zioVersion
)
```