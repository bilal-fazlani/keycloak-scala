package tech.bilal.keycloak.http.client.dto.req

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs

case class ClientScope(
    name: String,
    protocol: String,
    attributes: ClientScopeAttributes
)

object ClientScope {
  def apply(name: String): ClientScope                   = new ClientScope(name, "openid-connect", ClientScopeAttributes())
  implicit lazy val clientScopeCodec: Codec[ClientScope] = MapBasedCodecs.deriveCodec
}

case class ClientScopeAttributes(`display.on.consent.screen`: String, `include.in.token.scope`: String)

object ClientScopeAttributes {
  def apply(): ClientScopeAttributes                                         = new ClientScopeAttributes("true", "true")
  implicit lazy val clientScopeAttributesCodec: Codec[ClientScopeAttributes] = MapBasedCodecs.deriveCodec
}
