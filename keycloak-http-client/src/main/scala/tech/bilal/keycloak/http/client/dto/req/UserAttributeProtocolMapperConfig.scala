package tech.bilal.keycloak.http.client.dto.req

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs

case class UserAttributeProtocolMapperConfig(
    `user.attribute`: String,
    `claim.name`: String,
    `id.token.claim`: Boolean = true,
    `access.token.claim`: Boolean = true,
    `userinfo.token.claim`: Boolean = true,
    `jsonType.label`: String = "String"
)

object UserAttributeProtocolMapperConfig {
  implicit lazy val codec: Codec[UserAttributeProtocolMapperConfig] = MapBasedCodecs.deriveCodec
}
