package tech.bilal.keycloak.http.client.dto.req.protocol_mappers

import io.bullet.borer.Codec
import io.bullet.borer.derivation.CompactMapBasedCodecs

sealed trait ProtocolMapperConfig

object ProtocolMapperConfig {

  case class UserAttributeProtocolMapperConfig(
      `user.attribute`: String,
      `claim.name`: String,
      `id.token.claim`: Boolean,
      `access.token.claim`: Boolean,
      `userinfo.token.claim`: Boolean,
      `jsonType.label`: String
  ) extends ProtocolMapperConfig

  object UserAttributeProtocolMapperConfig {

    implicit lazy val codec: Codec[UserAttributeProtocolMapperConfig] = CompactMapBasedCodecs.deriveCodec

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
}
