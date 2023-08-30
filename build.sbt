val zioVersion          = "2.0.13"
val scala3Version       = "3.2.2"
val scala2Version       = "2.13.10"
val zioConfigVersion    = "4.0.0-RC16"
val nebulaClientVersion = "3.6.0"
val logbackVersion      = "1.4.5"

inThisBuild(
  List(
    scalaVersion     := scala3Version,
    homepage         := Some(url("https://github.com/hjfruit/zio-nebula")),
    licenses         := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
    organization     := "io.github.jxnu-liguobin",
    organizationName := "梦境迷离",
    developers       := List(
      Developer(
        id = "jxnu-liguobin",
        name = "梦境迷离",
        email = "dreamylost@outlook.com",
        url = url("https://github.com/jxnu-liguobin")
      )
    )
  )
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("check", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")

lazy val core = project
  .in(file("core"))
  .settings(
    name               := "zio-nebula",
    crossScalaVersions := Seq(scala3Version, scala2Version),
    libraryDependencies ++= Seq(
      "com.vesoft"     % "client"              % nebulaClientVersion,
      "dev.zio"       %% "zio-config-typesafe" % zioConfigVersion,
      "dev.zio"       %% "zio-config-magnolia" % zioConfigVersion,
      "dev.zio"       %% "zio"                 % zioVersion,
      "dev.zio"       %% "zio-test"            % zioVersion     % Test,
      "ch.qos.logback" % "logback-classic"     % logbackVersion % Test
    ),
    testFrameworks     := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )

lazy val examples = project
  .in(file("examples"))
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % logbackVersion
    )
  )
  .dependsOn(core % "compile->compile;test->test")

lazy val `zio-nebula` = project
  .in(file("."))
  .settings(
    publish / skip := true
  )
  .aggregate(
    core,
    examples
  )
