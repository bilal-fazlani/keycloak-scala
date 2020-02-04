package tech.bilal.keycloak.data

case class ApplicationUser(
    username: String,
    password: String,
    firstName: String = "",
    lastName: String = "",
    roles: Roles = Roles.empty
)
