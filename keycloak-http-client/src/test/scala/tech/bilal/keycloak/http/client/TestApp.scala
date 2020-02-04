package tech.bilal.keycloak.http.client

import tech.bilal.keycloak.http.client.model.{ClientRoles, ConfidentialClient}
import zio.DefaultRuntime

import scala.util.Random

object TestApp extends App {
  private val connectionInfo = KeycloakConnectionInfo("http", "localhost", 8081)
  private val httpClient     = new KeycloakHttpClient(connectionInfo)
  private val clientId       = s"demo-client_${Random.nextInt(1000).abs}"
  private val realm          = s"Demo_${Random.nextInt(1000).abs}"
  private val realmClient    = new RealmClient(connectionInfo, realm)
  private val roleName       = "super-admin"
  private val username       = "bilal"

  private val program = for {
    _     <- httpClient.createAdminUser("admin", "admin")
    token <- httpClient.getAccessToken("admin", "admin", "master")
    _     = println("logged in")
    _     <- realmClient.createRealm(overwrite = true)(token)
    _     <- realmClient.createClient(ConfidentialClient(clientId, clientId))(token)
    _     = print(s"client created: $clientId")
    _     <- realmClient.addUser(username, "bilal", "bilal", "fazlani")(token)
    _     = println(s"user created: $username")
    _     <- realmClient.addClientRole(clientId, roleName)(token)
    _     = println(s"created client role: $roleName for $clientId")
    _     <- realmClient.mapUserToClientRoles(username, ClientRoles(clientId, Set(roleName)))(token)
    _     = println("mapped client role to user")
  } yield ()

  val runtime = new DefaultRuntime {}
  runtime.unsafeRun(program)
}
