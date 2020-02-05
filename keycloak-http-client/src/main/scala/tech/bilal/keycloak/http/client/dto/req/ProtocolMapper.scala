package tech.bilal.keycloak.http.client.dto.req

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs

case class ProtocolMapper(
    name: String,
    config: UserAttributeProtocolMapperConfig,
    protocol: String = "openid-connect",
    protocolMapper: String = "oidc-usermodel-attribute-mapper"
)

object ProtocolMapper {
  implicit lazy val codec: Codec[ProtocolMapper] = MapBasedCodecs.deriveCodec
}
