package zio.nebula

final case class NebulaAuth(spaceName: String, username: String, password: String, reconnect: Boolean = true)
