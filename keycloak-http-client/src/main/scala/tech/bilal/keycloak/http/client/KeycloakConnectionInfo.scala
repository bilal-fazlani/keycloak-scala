package tech.bilal.keycloak.http.client

import sttp.model.Uri.UriContext

case class KeycloakConnectionInfo(scheme: String, host: String, port: Int) {
  def baseUrl = uri"$scheme://$host:$port/auth"
}
