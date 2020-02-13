package tech.bilal.keycloak.http.client

import sttp.model.Uri
import sttp.model.Uri.UriContext

case class KeycloakConnectionInfo(scheme: String, host: String, port: Int) {
  def baseUrl: Uri = uri"$scheme://$host:$port/auth"
}
