package tech.bilal.keycloak.data

case class ClientScope(name: String, protocolMappers: Set[ProtocolMapper], default: Boolean = false)
