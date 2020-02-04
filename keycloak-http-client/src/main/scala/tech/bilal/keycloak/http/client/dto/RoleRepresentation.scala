package tech.bilal.keycloak.http.client.dto

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs

case class RoleRepresentation(name: String, id: String, containerId: String, composite: Boolean, clientRole: Boolean)

object RoleRepresentation {
  implicit lazy val codec: Codec[RoleRepresentation] = MapBasedCodecs.deriveCodec
}
