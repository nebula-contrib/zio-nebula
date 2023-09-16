package zio.nebula

import zio._

/**
 * @author
 *   梦境迷离
 * @version 1.0,2023/9/14
 */
object GlobalSettings {

  var printStatement: Boolean = false
  var logLevel: LogLevel      = LogLevel.Warning

  def printLog(stmt: String): ZIO[Any, Nothing, Unit] =
    ZIO
      .when(GlobalSettings.printStatement) {
        if (GlobalSettings.logLevel == LogLevel.Error) {
          ZIO.logError(s"Nebula client executed statement: $stmt")
        } else if (GlobalSettings.logLevel == LogLevel.Warning) {
          ZIO.logWarning(s"Nebula client executed statement: $stmt")
        } else if (GlobalSettings.logLevel == LogLevel.Info) {
          ZIO.logInfo(s"Nebula client executed statement: $stmt")
        } else if (GlobalSettings.logLevel == LogLevel.Debug) {
          ZIO.logDebug(s"Nebula client executed statement: $stmt")
        } else if (GlobalSettings.logLevel == LogLevel.Trace) {
          ZIO.logTrace(s"Nebula client executed statement: $stmt")
        } else
          ZIO.logFatal(s"Nebula client executed statement: $stmt").when(GlobalSettings.logLevel == LogLevel.Fatal).unit
      }
      .unit
}
