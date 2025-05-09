package nebula4scala.data.meta

import com.vesoft.nebula.client.graph.data
import com.vesoft.nebula.client.graph.data.{ CASignedSSLParam, SelfSignedSSLParam }

sealed trait SSLParam {
  self =>

  def toJava: data.SSLParam =
    self match {
      case SSLParam.CASignedSSL(caCrtFilePath, crtFilePath, keyFilePath) =>
        new CASignedSSLParam(caCrtFilePath, crtFilePath, keyFilePath)
      case SSLParam.SelfSignedSSL(crtFilePath, keyFilePath, password) =>
        new SelfSignedSSLParam(crtFilePath, keyFilePath, password)
    }

}

object SSLParam {

  final case class CASignedSSL(caCrtFilePath: String, crtFilePath: String, keyFilePath: String) extends SSLParam

  final case class SelfSignedSSL(crtFilePath: String, keyFilePath: String, password: String) extends SSLParam
}
