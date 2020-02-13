package tech.bilal.keycloak.data.intermediate

sealed trait IntermediateProtocolMapper

case class UserAttribute(attributeName: String) extends IntermediateProtocolMapper
