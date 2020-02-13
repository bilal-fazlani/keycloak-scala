package tech.bilal.keycloak.data

sealed trait ProtocolMapper {
  val name: String
}

case class UserAttribute(name: String, attributeName: String) extends ProtocolMapper
