package tech.bilal.keycloak.data.intermediate

private[data] case class IntermediateApplicationUser(
    password: String,
    firstName: String = "",
    lastName: String = "",
    attributes: Map[String, String] = Map.empty,
    roles: IntermediateRoles = IntermediateRoles.empty
)
