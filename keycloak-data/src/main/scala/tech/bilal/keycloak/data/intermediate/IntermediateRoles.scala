package tech.bilal.keycloak.data.intermediate

private[data] case class IntermediateRoles(realmRoles: Set[String] = Set.empty, clientRoles: Map[String, Set[String]] = Map.empty)

private[data] object IntermediateRoles {
  val empty: IntermediateRoles = IntermediateRoles()
}
