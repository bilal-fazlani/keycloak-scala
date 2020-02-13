package tech.bilal.keycloak.embedded

import tech.bilal.keycloak.data.KeycloakData
import tech.bilal.keycloak.http.client.KeycloakConnectionInfo
import zio.IO

class EmbeddedKeycloak(keycloakConnectionInfo: KeycloakConnectionInfo) {
  def startAndSeedData: IO[Any, StopHandle] = {
    for {
      data <- KeycloakData.fromConfig()
      _    <- data.pushTo(keycloakConnectionInfo, overwrite = true)
    } yield new StopHandle(keycloakConnectionInfo, data)
  }
}
