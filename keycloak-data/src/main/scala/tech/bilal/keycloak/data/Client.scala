package tech.bilal.keycloak.data

sealed trait Client {
  val id: String
  val name: String
  val roles: Seq[String]
}

object Client {
  case class BearerOnly(id: String, name: String, roles: Seq[String] = Seq.empty) extends Client

  case class Public(
      id: String,
      name: String,
      directAccessGrant: Boolean = true,
      implicitFlow: Boolean = false,
      standardFlow: Boolean = true,
      webOrigins: Seq[String] = Seq.empty,
      redirectUris: Seq[String] = Seq.empty,
      roles: Seq[String] = Seq.empty
  ) extends Client

  case class Confidential(
      id: String,
      name: String,
      directAccessGrant: Boolean = true,
      implicitFlow: Boolean = false,
      standardFlow: Boolean = true,
      serviceAccounts: Boolean = false,
      webOrigins: Seq[String] = Seq.empty,
      redirectUris: Seq[String] = Seq.empty,
      roles: Seq[String] = Seq.empty
  ) extends Client

}
