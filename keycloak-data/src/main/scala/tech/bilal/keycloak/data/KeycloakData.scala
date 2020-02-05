package tech.bilal.keycloak.data

import pureconfig.error.ConfigReaderFailures
import tech.bilal.keycloak.data.rw.{Publisher, Reader}
import tech.bilal.keycloak.http.client.{ApiError, KeycloakConnectionInfo}
import zio.IO

case class KeycloakData(admin: AdminUser = AdminUser.default, realms: Set[Realm] = Set.empty) {
  def pushTo(keycloakConnectionInfo: KeycloakConnectionInfo, overwrite: Boolean): IO[ApiError, Unit] =
    new Publisher().pushTo(this, keycloakConnectionInfo, overwrite)
}

object KeycloakData {
  val empty: KeycloakData                                                = KeycloakData()
  def fromConfig(): IO[ConfigReaderFailures, KeycloakData]               = new Reader().readConfig
  def fromFile(filePath: String): IO[ConfigReaderFailures, KeycloakData] = new Reader().readFile(filePath)
}
