package tech.bilal.keycloak.http.client.dto.req

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs
import tech.bilal.keycloak.http.client.model.{BearerOnlyClient, Client, ConfidentialClient, PublicClient}

case class CreateClient(
    clientId: String,
    name: String,
    redirectUris: Seq[String],
    protocol: String,
    enabled: Boolean,
    webOrigins: Seq[String],
    standardFlowEnabled: Boolean,
    implicitFlowEnabled: Boolean,
    directAccessGrantsEnabled: Boolean,
    serviceAccountsEnabled: Boolean,
    //CLIENT TYPE,
    bearerOnly: Boolean,
    publicClient: Boolean
)

object CreateClient {
  def apply(client: Client): CreateClient =
    client match {
      case BearerOnlyClient(id, name) =>
        new CreateClient(
          clientId = id,
          name = name,
          redirectUris = Seq.empty,
          protocol = "openid-connect",
          enabled = true,
          bearerOnly = true,
          publicClient = false,
          webOrigins = Seq.empty,
          standardFlowEnabled = true,
          implicitFlowEnabled = false,
          directAccessGrantsEnabled = false,
          serviceAccountsEnabled = false
        )
      case PublicClient(id, name, directAccessGrant, implicitFlow, standardFlow, webOrigins, redirectUris) =>
        new CreateClient(
          clientId = id,
          name = name,
          redirectUris = redirectUris,
          protocol = "openid-connect",
          enabled = true,
          bearerOnly = false,
          publicClient = true,
          webOrigins = webOrigins,
          standardFlowEnabled = standardFlow,
          implicitFlowEnabled = implicitFlow,
          directAccessGrantsEnabled = directAccessGrant,
          serviceAccountsEnabled = false
        )
      case ConfidentialClient(
          id,
          name,
          directAccessGrant,
          implicitFlow,
          standardFlow,
          serviceAccounts,
          webOrigins,
          redirectUris
          ) =>
        new CreateClient(
          clientId = id,
          name = name,
          redirectUris = redirectUris,
          protocol = "openid-connect",
          enabled = true,
          bearerOnly = false,
          publicClient = false,
          webOrigins = webOrigins,
          standardFlowEnabled = standardFlow,
          implicitFlowEnabled = implicitFlow,
          directAccessGrantsEnabled = directAccessGrant,
          serviceAccountsEnabled = serviceAccounts
        )
    }

  implicit lazy val codec: Codec[CreateClient] = MapBasedCodecs.deriveCodec
}
