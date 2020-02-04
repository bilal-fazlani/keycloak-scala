package tech.bilal.keycloak.http.client.dto.req

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs

case class CreateUser(
    enabled: Boolean,
    username: String,
    attributes: Map[String, String],
    emailVerified: String,
    firstName: String,
    lastName: String
)

object CreateUser {
  implicit lazy val codec: Codec[CreateUser] = MapBasedCodecs.deriveCodec
}
