package tech.bilal.keycloak.data

case class ApplicationUser(
    username: String,
    password: String,
    firstName: String = "",
    lastName: String = "",
    attributes: Map[String, String] = Map.empty,
    roles: Roles = Roles.empty
)
