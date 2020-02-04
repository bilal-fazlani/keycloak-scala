package tech.bilal.keycloak.http.client.dto.req

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs

case class CreateRealm(enabled: Boolean, id: String, realm: String)

object CreateRealm {
  implicit lazy val codec: Codec[CreateRealm] = MapBasedCodecs.deriveCodec
}
