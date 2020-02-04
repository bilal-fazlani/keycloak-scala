package tech.bilal.keycloak.data.intermediate

private[data] sealed trait IntermediateClient {
  val name: String
  val roles: Seq[String]
}

private[data] object IntermediateClient {
  case class BearerOnly(name: String, roles: Seq[String] = Seq.empty) extends IntermediateClient

  case class Public(
      name: String,
      directAccessGrant: Boolean = true,
      implicitFlow: Boolean = false,
      standardFlow: Boolean = true,
      webOrigins: Seq[String] = Seq.empty,
      redirectUris: Seq[String] = Seq.empty,
      roles: Seq[String] = Seq.empty
  ) extends IntermediateClient

  case class Confidential(
      name: String,
      directAccessGrant: Boolean = true,
      implicitFlow: Boolean = false,
      standardFlow: Boolean = true,
      serviceAccounts: Boolean = false,
      webOrigins: Seq[String] = Seq.empty,
      redirectUris: Seq[String] = Seq.empty,
      roles: Seq[String] = Seq.empty
  ) extends IntermediateClient
}
