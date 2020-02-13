package tech.bilal.keycloak.data

case class Realm(
    name: String,
    roles: Set[String] = Set.empty,
    clients: Set[Client] = Set.empty,
    users: Set[ApplicationUser] = Set.empty,
    clientScopes: Set[ClientScope] = Set.empty
)
