package tech.bilal.keycloak.data.intermediate

private[data] case class IntermediateApplicationUser(
    password: String,
    firstName: String = "",
    lastName: String = "",
    roles: IntermediateRoles = IntermediateRoles.empty
)
