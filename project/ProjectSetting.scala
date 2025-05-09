import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt.*
import sbt.Keys.*

object ProjectSetting {

  val value: Seq[Def.Setting[?]] = Seq(
    scalacOptions ++= Seq(
      "-unchecked",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-deprecation",
      "-Xkind-projector:underscores",
      "-Yretain-trees",
      "-Xmax-inlines:512"
    ),
    autoAPIMappings          := true,
    version                  := (ThisBuild / version).value,
    Test / parallelExecution := false, // see https://www.scalatest.org/user_guide/async_testing
    Global / cancelable      := true,
    exportJars               := true,
    scalafmtOnCompile        := true
  )

  val noPublish: Seq[Def.Setting[?]] = Seq(
    publish / skip := true
  )

}
