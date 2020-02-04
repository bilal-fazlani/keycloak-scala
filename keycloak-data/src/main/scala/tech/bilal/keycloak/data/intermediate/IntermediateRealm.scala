package tech.bilal.keycloak.data.intermediate

private[data] case class IntermediateRealm(
    roles: Set[String] = Set.empty,
    clients: Map[String, IntermediateClient] = Map.empty,
    users: Map[String, IntermediateApplicationUser] = Map.empty
)
