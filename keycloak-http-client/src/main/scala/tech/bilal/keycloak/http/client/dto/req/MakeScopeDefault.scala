package tech.bilal.keycloak.http.client.dto.req

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs

case class MakeScopeDefault(realm: String, clientScopeId: String)

object MakeScopeDefault {
  implicit lazy val makeScopeDefaultCodec: Codec[MakeScopeDefault] = MapBasedCodecs.deriveCodec
}
