package tech.bilal.keycloak.data.intermediate

case class IntermediateClientScope(protocolMappers: Map[String, IntermediateProtocolMapper], default: Boolean = false)
