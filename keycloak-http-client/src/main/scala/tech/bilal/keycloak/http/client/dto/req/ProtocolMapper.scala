package tech.bilal.keycloak.http.client.dto.req

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs

case class ProtocolMapper(
    name: String,
    config: UserAttributeProtocolMapperConfig,
    protocol: String,
    protocolMapper: String
)

object ProtocolMapper {
  implicit lazy val codec: Codec[ProtocolMapper] = MapBasedCodecs.deriveCodec

  def apply(name: String, config: UserAttributeProtocolMapperConfig): ProtocolMapper =
    new ProtocolMapper(name, config, "openid-connect", "oidc-usermodel-attribute-mapper")
}

case class UserAttributeProtocolMapperConfig(
    `user.attribute`: String,
    `claim.name`: String,
    `id.token.claim`: Boolean,
    `access.token.claim`: Boolean,
    `userinfo.token.claim`: Boolean,
    `jsonType.label`: String
)

object UserAttributeProtocolMapperConfig {
  implicit lazy val codec: Codec[UserAttributeProtocolMapperConfig] = MapBasedCodecs.deriveCodec

  def apply(
      `user.attribute`: String,
      `claim.name`: String
  ): UserAttributeProtocolMapperConfig =
    new UserAttributeProtocolMapperConfig(
      `user.attribute`,
      `claim.name`,
      true,
      true,
      true,
      "String"
    )
}
