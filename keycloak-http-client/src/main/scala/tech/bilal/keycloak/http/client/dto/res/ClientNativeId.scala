package tech.bilal.keycloak.http.client.dto.res

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs

case class ClientNativeId(id: String, clientId: String)
object ClientNativeId {
  implicit lazy val codec: Codec[ClientNativeId] = MapBasedCodecs.deriveCodec
}
