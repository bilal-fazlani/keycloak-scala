package tech.bilal.keycloak.data

case class AdminUser(username: String = "admin", password: String = "admin")

object AdminUser {
  val default: AdminUser = AdminUser()
}
