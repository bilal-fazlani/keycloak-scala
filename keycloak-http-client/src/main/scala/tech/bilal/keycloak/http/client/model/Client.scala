package tech.bilal.keycloak.http.client.model

sealed trait Client

case class BearerOnlyClient(id: String, name: String) extends Client

case class PublicClient(
    id: String,
    name: String,
    directAccessGrant: Boolean = true,
    implicitFlow: Boolean = false,
    standardFlow: Boolean = true,
    webOrigins: Seq[String] = Seq.empty,
    redirectUris: Seq[String] = Seq.empty
) extends Client

case class ConfidentialClient(
    id: String,
    name: String,
    directAccessGrant: Boolean = true,
    implicitFlow: Boolean = false,
    standardFlow: Boolean = true,
    serviceAccounts: Boolean = false,
    webOrigins: Seq[String] = Seq.empty,
    redirectUris: Seq[String] = Seq.empty
) extends Client
