package tech.bilal.keycloak.http.client.dto.res

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs

case class TokenResponse(access_token: String)

object TokenResponse {
  implicit lazy val tokenResponseCodec: Codec[TokenResponse] = MapBasedCodecs.deriveCodec
}
