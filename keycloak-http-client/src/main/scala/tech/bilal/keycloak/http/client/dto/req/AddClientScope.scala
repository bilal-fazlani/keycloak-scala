package tech.bilal.keycloak.http.client.dto.req

import io.bullet.borer.{Codec, Json}
import io.bullet.borer.derivation.MapBasedCodecs

case class AddClientScope(
    name: String,
    protocol: String,
    attributes: AddClientScopeAttributes
)

object AddClientScope {
  def apply(name: String): AddClientScope                      = new AddClientScope(name, "openid-connect", AddClientScopeAttributes())
  implicit lazy val addClientScopeCodec: Codec[AddClientScope] = MapBasedCodecs.deriveCodec
}

case class AddClientScopeAttributes(`display.on.consent.screen`: String, `include.in.token.scope`: String)

object AddClientScopeAttributes {
  def apply(): AddClientScopeAttributes                                            = new AddClientScopeAttributes("true", "true")
  implicit lazy val addClientScopeAttributesCodec: Codec[AddClientScopeAttributes] = MapBasedCodecs.deriveCodec
}
