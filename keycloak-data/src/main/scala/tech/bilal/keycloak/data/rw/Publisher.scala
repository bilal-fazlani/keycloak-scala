package tech.bilal.keycloak.data.rw

import sttp.model.StatusCode
import tech.bilal.keycloak.data
import tech.bilal.keycloak.data.Client.{BearerOnly, Confidential, Public}
import tech.bilal.keycloak.data.{KeycloakData, UserAttribute, ProtocolMapper => ModelProtocolMapper}
import tech.bilal.keycloak.http.client.ApiError.RequestError
import tech.bilal.keycloak.http.client._
import tech.bilal.keycloak.http.client.dto.req.protocol_mappers.ProtocolMapper
import tech.bilal.keycloak.http.client.dto.req.protocol_mappers.ProtocolMapper.UserAttributeProtocolMapper
import tech.bilal.keycloak.http.client.dto.req.protocol_mappers.ProtocolMapperConfig.UserAttributeProtocolMapperConfig
import tech.bilal.keycloak.http.client.dto.res.TokenResponse
import tech.bilal.keycloak.http.client.model.{BearerOnlyClient, ClientRoles, ConfidentialClient, PublicClient}
import zio.{IO, UIO, ZIO}

class Publisher {
  def pushTo(
      keycloakData: KeycloakData,
      keycloakConnectionInfo: KeycloakConnectionInfo,
      overwrite: Boolean
  ): IO[ApiError, Unit] = {
    import keycloakData._
    val keycloakHttpClient = new KeycloakHttpClient(keycloakConnectionInfo)

    val createAdmin = keycloakHttpClient
      .createAdminUser(admin.username, admin.password)
      .orElse(UIO.unit)

    createAdmin *> ZIO
      .foreach(realms) { realm =>
        val realmClient = new RealmClient(keycloakConnectionInfo, realm.name)

        val addRealmRoles: TokenResponse => IO[RequestError, List[StatusCode]] =
          token => ZIO.foreach(realm.roles)(realmClient.addRealmRole(_)(token))

        val addUsers: TokenResponse => IO[ApiError, List[String]] =
          token =>
            ZIO.foreach(realm.users)(u =>
              realmClient.addUser(u.username, u.password, u.firstName, u.lastName, u.attributes)(token)
            )

        val addClients: TokenResponse => IO[ApiError, List[List[StatusCode]]] = token =>
          ZIO.foreach(realm.clients) { client =>
            realmClient.createClient(convertClient(client))(token) *>
              ZIO.foreach(client.roles)(realmClient.addClientRole(client.id, _)(token))
          }

        val mapRealmRolesToUsers: TokenResponse => IO[ApiError, List[StatusCode]] =
          token => IO.foreach(realm.users)(u => realmClient.mapUserToRealmRoles(u.username, u.roles.realmRoles)(token))

        val mapClientRolesToUsers: TokenResponse => IO[ApiError, List[Unit]] =
          token =>
            IO.foreach(realm.users)(u =>
              realmClient.mapUserToClientRoles(
                u.username,
                u.roles.clientRoles.map(cr => ClientRoles(cr.clientId, cr.roles)).toSeq: _*
              )(token)
            )

        val addClientScopes: TokenResponse => IO[ApiError, List[String]] = (token: TokenResponse) =>
          IO.foreach(realm.clientScopes)(cs =>
            realmClient
              .createClientScope(cs.name, cs.protocolMappers.map(convertProtocolMapperConfig), default = cs.default)(token)
          )

        for {
          token <- keycloakHttpClient.getAccessToken(admin.username, admin.password, "master")
          _     <- realmClient.createRealm(overwrite)(token)
          _     <- addClientScopes(token)
          _     <- ZIO.collectAllPar(Seq(addRealmRoles(token), addUsers(token), addClients(token)))
          _     <- ZIO.collectAllPar(Seq(mapRealmRolesToUsers(token), mapClientRolesToUsers(token)))
        } yield ()
      }
      .unit
  }

  private def convertProtocolMapperConfig(protocolMapper: ModelProtocolMapper): ProtocolMapper = protocolMapper match {
    case UserAttribute(name, attributeName) =>
      UserAttributeProtocolMapper(name, UserAttributeProtocolMapperConfig(attributeName, attributeName))
  }

  private def convertClient(client: data.Client): model.Client = {
    client match {
      case BearerOnly(id, name, _) =>
        BearerOnlyClient(id, name)
      case Public(id, name, directAccessGrant, implicitFlow, standardFlow, webOrigins, redirectUris, _) =>
        PublicClient(id, name, directAccessGrant, implicitFlow, standardFlow, webOrigins, redirectUris)
      case Confidential(
          id,
          name,
          directAccessGrant,
          implicitFlow,
          standardFlow,
          serviceAccounts,
          webOrigins,
          redirectUris,
          _
          ) =>
        ConfidentialClient(id, name, directAccessGrant, implicitFlow, standardFlow, serviceAccounts, webOrigins, redirectUris)
    }
  }
}
