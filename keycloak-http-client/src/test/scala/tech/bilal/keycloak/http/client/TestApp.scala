package tech.bilal.keycloak.http.client

import tech.bilal.keycloak.http.client.dto.req.protocol_mappers.ProtocolMapper.UserAttributeProtocolMapper
import tech.bilal.keycloak.http.client.dto.req.protocol_mappers.ProtocolMapperConfig.UserAttributeProtocolMapperConfig
import tech.bilal.keycloak.http.client.model.{ClientRoles, ConfidentialClient}
import zio.console._
import zio.{UIO, ZIO}

import scala.util.Random

object TestApp extends zio.App {
  private val connectionInfo     = KeycloakConnectionInfo("http", "localhost", 8081)
  private val httpClient         = new KeycloakHttpClient(connectionInfo)
  private val clientId           = s"demo-client_${Random.nextInt(1000).abs}"
  private val realm              = "http-test"
  private val realmClient        = new RealmClient(connectionInfo, realm)
  private val roleName           = "super-admin"
  private val username           = "bilal"
  private val clientScopeName    = "client-scope1"
  private val protocolMapperName = "protocol-mapper1"
  private val attributeName      = "city"

  private val program = for {
    _     <- httpClient.createAdminUser("admin", "admin").ignore
    token <- httpClient.getAccessToken("admin", "admin", "master")
    _     <- putStrLn("logged in")
//    _     <- realmClient.deleteRealm(token).ignore
    _ <- realmClient.createRealm(overwrite = true)(token)
    _ <- putStrLn(s"realm created $realm")
    _ <- realmClient
          .createClientScope(
            clientScopeName,
            Set(UserAttributeProtocolMapper(protocolMapperName, UserAttributeProtocolMapperConfig(attributeName, attributeName)))
          )(token)
    _ = println(s"created clientScope $clientScopeName with protocol mapper $protocolMapperName for attribute $attributeName")
    _ <- realmClient.createClient(ConfidentialClient(clientId, clientId))(token)
    _ <- putStrLn(s"client created: $clientId")
    _ <- realmClient.addUser(username, "bilal", "bilal", "fazlani", Map.empty)(token)
    _ <- putStrLn(s"user created: $username")
    _ <- realmClient.addClientRole(clientId, roleName)(token)
    _ <- putStrLn(s"created client role: $roleName for $clientId")
    _ <- realmClient.mapUserToClientRoles(username, ClientRoles(clientId, Set(roleName)))(token)
    _ <- putStrLn("mapped client role to user")
  } yield (0)

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    program
      .mapError(_.toString)
      .tapError(putStrLn)
      .orElse(UIO.succeed(1))
}
