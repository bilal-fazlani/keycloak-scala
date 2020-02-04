package tech.bilal.keycloak.data

case class Roles(realmRoles: Set[String] = Set.empty, clientRoles: Set[ClientRoles] = Set.empty)

object Roles {
  val empty: Roles = Roles()
}
