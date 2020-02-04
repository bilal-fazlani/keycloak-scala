package tech.bilal.keycloak.embedded

import tech.bilal.keycloak.data.KeycloakData
import tech.bilal.keycloak.http.client.{ApiError, KeycloakConnectionInfo, KeycloakHttpClient, RealmClient}
import zio.IO

class StopHandle private[embedded] (keycloakConnectionInfo: KeycloakConnectionInfo, keycloakData: KeycloakData) {
  def stopAndClean: IO[ApiError, Unit] = {
    for {
      token <- new KeycloakHttpClient(keycloakConnectionInfo)
                .getAccessToken(keycloakData.admin.username, keycloakData.admin.password, "master")
      _ <- IO.foreach(keycloakData.realms) { realm =>
            val realmClient = new RealmClient(keycloakConnectionInfo, realm.name)
            realmClient.deleteRealm(token)
          }
      masterClient = new RealmClient(keycloakConnectionInfo, "master")
      _            <- masterClient.deleteUser(keycloakData.admin.username)(token)
    } yield ()
  }
}
