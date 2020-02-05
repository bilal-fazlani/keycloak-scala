package tech.bilal.keycloak.http.client

import sttp.client._
import sttp.model.StatusCode
import tech.bilal.keycloak.http.client.ApiError.RequestError
import tech.bilal.keycloak.http.client.dto.RoleRepresentation
import tech.bilal.keycloak.http.client.dto.req.{CreateClient, CreateRealm, CreateUser, UserAttributeProtocolMapperConfig}
import tech.bilal.keycloak.http.client.dto.res.{ClientNativeId, TokenResponse, UserNativeId}
import tech.bilal.keycloak.http.client.model.{Client, ClientRoles}
import zio.{IO, UIO, ZIO}

class RealmClient(keycloakConnectionInfo: KeycloakConnectionInfo, realm: String)
    extends KeycloakHttpClient(keycloakConnectionInfo) {

  import keycloakConnectionInfo._

  val realmUrl = uri"$baseUrl/admin/realms/$realm"

  def deleteUser(username: String)(implicit token: TokenResponse): IO[ApiError, StatusCode] =
    for {
      userId <- getUserId(username)
      status <- sendAndGetStatus(delete(())(uri"$realmUrl/users/$userId"))
    } yield status

  def createRealm(overwrite: Boolean)(implicit token: TokenResponse): IO[RequestError, StatusCode] =
    sendAndGetStatus(post(CreateRealm(id = realm, realm = realm, enabled = true))(uri"$baseUrl/admin/realms"))
      .flipWith(flipped =>
        flipped.flatMap {
          case RequestError(Some(StatusCode.Conflict), _) if overwrite =>
            (deleteRealm *> createRealm(overwrite = false)).flip
          case e: RequestError => IO.fail(e).flip
        }
      )

  def deleteRealm(implicit token: TokenResponse): IO[RequestError, StatusCode] =
    sendAndGetStatus(delete(())(realmUrl))

  def createClient(client: Client)(implicit token: TokenResponse): IO[RequestError, String] =
    for {
      res      <- send(post(CreateClient(client))(uri"$realmUrl/clients"))
      location <- IO.fromOption(res.header("Location")).mapError(_ => RequestError(Some(res.code), "no location in header"))
      clientId = location.split("/").last
    } yield clientId

  def addClientRole(clientId: String, roleName: String)(implicit token: TokenResponse): IO[ApiError, StatusCode] = {
    for {
      cid    <- getClientNativeId(clientId)
      status <- sendAndGetStatus(post(Map("name" -> roleName))(uri"$realmUrl/clients/$cid/roles"))
    } yield status
  }

  def addRealmRole(roleName: String)(implicit token: TokenResponse): IO[RequestError, StatusCode] =
    sendAndGetStatus(post(Map("name" -> roleName))(uri"$realmUrl/roles"))

  /**
    * Creates user and returns user id
    */
  def addUser(userName: String, password: String, firstName: String, lastName: String, attributes: Map[String, String])(
      implicit token: TokenResponse
  ): IO[RequestError, String] =
    for {
      res <- send(post(CreateUser(enabled = true, userName, attributes.map {
              case (key, value) => (key, Seq(value))
            }, "", firstName, lastName))(uri"$realmUrl/users"))
      location <- IO.fromOption(res.header("location")).mapError(_ => RequestError(Some(res.code), "user location not found"))
      userId   = location.split("/").last
      _ <- send(
            put(Map("type" -> "password", "value" -> password, "temporary" -> "false"))(
              uri"$realmUrl/users/$userId/reset-password"
            )
          )
    } yield userId

  def mapUserToClientRoles(username: String, roleMappings: ClientRoles*)(
      implicit token: TokenResponse
  ): IO[ApiError, Unit] =
    for {
      userId <- getUserId(username)
      _ <- ZIO.foreach(roleMappings) {
            case ClientRoles(clientId, roles) =>
              for {
                cid <- getClientNativeId(clientId)
                allRoles <- sendAndDecode[Set[RoleRepresentation]](
                             get(uri"$realmUrl/users/$userId/role-mappings/clients/$cid/available")
                           )
                filtered = allRoles.filter(r => roles.contains(r.name))
                _ <- if (filtered.size != roles.size)
                      IO.fail(
                        RequestError(None, s"given set of client roles for $cid contains one or more invalid entries: $roles")
                      )
                    else UIO.unit
                status <- sendAndGetStatus(
                           post[Set[RoleRepresentation]](filtered)(
                             uri"$realmUrl/users/$userId/role-mappings/clients/$cid"
                           )
                         )
              } yield status
          }
    } yield ()

  def mapUserToRealmRoles(username: String, roles: Set[String])(implicit token: TokenResponse): IO[ApiError, StatusCode] =
    for {
      userId        <- getUserId(username)
      allRolesRoles <- sendAndDecode(get[Set[RoleRepresentation]](uri"$realmUrl/roles"))
      filtered      = allRolesRoles.filter(r => roles.contains(r.name))
      _ <- if (filtered.size != roles.size)
            IO.fail(RequestError(None, s"given set of realm roles contains one or more invalid entries: $roles"))
          else UIO.unit
      status <- sendAndGetStatus(post(filtered)(uri"$realmUrl/users/$userId/role-mappings/realm"))
    } yield status

  private def getClientNativeId(clientId: String)(implicit token: TokenResponse): IO[ApiError, String] = {
    for {
      allClients <- sendAndDecode[Array[ClientNativeId]](get(uri"$realmUrl/clients?viewableOnly=true"))
      client <- IO
                 .fromOption(allClients.find(_.clientId == clientId))
                 .mapError(_ => RequestError(None, s"invalid client id: $clientId"))
    } yield client.id
  }

  private def getUserId(username: String)(implicit token: TokenResponse): IO[ApiError, String] = {
    sendAndDecode(get[Seq[UserNativeId]](uri"$realmUrl/users?username=$username"))
      .map(_.head.id)
  }
}
