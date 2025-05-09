import sbt.ThisBuild
import xerial.sbt.Sonatype.sonatypeCentralHost

val zioVersion          = "2.1.14"
val scala3_Version      = "3.6.3"
val scala2_13Version    = "2.13.16"
val scala2_12Version    = "2.12.20"
val zioConfigVersion    = "4.0.4"
val nebulaClientVersion = "3.8.4"
val catsVersion         = "2.9.0"
val catsEffectVersion   = "3.5.7"
val pureconfigVersion   = "0.17.8"

val logbackVersion              = "1.4.11"
val silencerVersion             = "1.4.2"
val testcontainersNebulaVersion = "0.2.0"

val supportCrossVersionList = Seq(scala3_Version, scala2_13Version, scala2_12Version)

inThisBuild(
  List(
    scalaVersion     := supportCrossVersionList.head,
    homepage         := Some(url("https://github.com/nebula-contrib/zio-nebula")),
    licenses         := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
    organization     := "io.github.jxnu-liguobin",
    organizationName := "梦境迷离",
    developers := List(
      Developer(
        id = "jxnu-liguobin",
        name = "梦境迷离",
        email = "dreamylost@outlook.com",
        url = url("https://github.com/jxnu-liguobin")
      )
    ),
    ThisBuild / sonatypeProfileName    := "io.github.jxnu-liguobin",
    ThisBuild / sonatypeCredentialHost := sonatypeCentralHost
  )
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")

lazy val _zioTests = Seq(
  "dev.zio" %% "zio-test-magnolia" % zioVersion,
  "dev.zio" %% "zio-test"          % zioVersion,
  "dev.zio" %% "zio-test-sbt"      % zioVersion
)

lazy val `core` = project
  .in(file("modules/core"))
  .settings(
    name               := "nebula4scala-core",
    crossScalaVersions := supportCrossVersionList,
    libraryDependencies ++= Seq(
      "com.vesoft"             % "client"                    % nebulaClientVersion,
      "com.github.pureconfig" %% "pureconfig-core"           % pureconfigVersion,
      "com.github.pureconfig" %% "pureconfig-generic-scala3" % pureconfigVersion
    )
  )

lazy val `cats` = project
  .in(file("modules/cats"))
  .settings(
    name               := "nebula4scala-cats",
    crossScalaVersions := supportCrossVersionList,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % catsEffectVersion
    )
  )
  .settings(ProjectSetting.value)
  .dependsOn(`core`)

lazy val `zio` = project
  .in(file("modules/zio"))
  .settings(
    name               := "nebula4scala-zio",
    crossScalaVersions := supportCrossVersionList,
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
      "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
      "dev.zio" %% "zio"                 % zioVersion,
      // see https://github.com/zio/zio-config/issues/1245
      "com.github.ghik" %% "silencer-lib"    % silencerVersion % Provided cross CrossVersion.for3Use2_13,
      "ch.qos.logback"   % "logback-classic" % logbackVersion  % Test,
      "io.github.jxnu-liguobin" %% "testcontainers-nebula" % testcontainersNebulaVersion % Test
    ) ++ _zioTests.map(_ % Test),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
  .settings(ProjectSetting.value)
  .dependsOn(`core`)

lazy val examples = project
  .in(file("examples"))
  .settings(
    crossScalaVersions := supportCrossVersionList,
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % logbackVersion
    )
  )
  .settings(ProjectSetting.noPublish)
  .dependsOn(`zio` % "compile->compile;test->test")
  .dependsOn(`cats` % "compile->compile;test->test")

lazy val `root` = project
  .in(file("."))
  .settings(
    crossScalaVersions := Nil,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++= _zioTests.map(_ % Test)
  )
  .settings(ProjectSetting.noPublish)
  .aggregate(
    `zio`,
    `core`,
    `cats`,
    examples
  )
