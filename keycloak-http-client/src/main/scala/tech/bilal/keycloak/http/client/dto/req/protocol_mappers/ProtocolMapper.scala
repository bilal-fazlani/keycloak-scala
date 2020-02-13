package tech.bilal.keycloak.http.client.dto.req.protocol_mappers

import io.bullet.borer.derivation.{MapBasedCodecs, key}
import io.bullet.borer.{AdtEncodingStrategy, Codec}
import tech.bilal.keycloak.http.client.dto.req.protocol_mappers.ProtocolMapperConfig.UserAttributeProtocolMapperConfig

sealed trait ProtocolMapper

object ProtocolMapper {

  private implicit val flatAdtEncoding: AdtEncodingStrategy    = AdtEncodingStrategy.flat(typeMemberName = "protocolMapper")
  implicit lazy val protocolMapperCodec: Codec[ProtocolMapper] = MapBasedCodecs.deriveAllCodecs

  @key("oidc-usermodel-attribute-mapper")
  case class UserAttributeProtocolMapper(
      name: String,
      config: UserAttributeProtocolMapperConfig,
      protocol: String
  ) extends ProtocolMapper

  object UserAttributeProtocolMapper {
    def apply(name: String, config: UserAttributeProtocolMapperConfig): UserAttributeProtocolMapper =
      new UserAttributeProtocolMapper(name, config, "openid-connect")
  }
}
