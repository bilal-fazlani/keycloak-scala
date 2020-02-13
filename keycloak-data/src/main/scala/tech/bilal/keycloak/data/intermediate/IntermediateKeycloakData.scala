package tech.bilal.keycloak.data.intermediate

import tech.bilal.keycloak.data._

private[data] case class IntermediateKeycloakData(
    adminUser: AdminUser = AdminUser.default,
    realms: Map[String, IntermediateRealm] = Map.empty
) {
  def toKeycloakData: KeycloakData = {
    KeycloakData(
      adminUser,
      realms.map {
        case (realmName, IntermediateRealm(roles, clients, users, clientScopes)) =>
          Realm(
            realmName,
            roles,
            clients.map {
              case (clientId, intClient) =>
                intClient match {
                  case IntermediateClient.BearerOnly(name, roles) => Client.BearerOnly(clientId, name, roles)
                  case IntermediateClient
                        .Public(name, directAccessGrant, implicitFlow, standardFlow, webOrigins, redirectUris, roles) =>
                    Client.Public(clientId, name, directAccessGrant, implicitFlow, standardFlow, webOrigins, redirectUris, roles)
                  case IntermediateClient.Confidential(
                      name,
                      directAccessGrant,
                      implicitFlow,
                      standardFlow,
                      serviceAccounts,
                      webOrigins,
                      redirectUris,
                      roles
                      ) =>
                    Client.Confidential(
                      clientId,
                      name,
                      directAccessGrant,
                      implicitFlow,
                      standardFlow,
                      serviceAccounts,
                      webOrigins,
                      redirectUris,
                      roles
                    )
                }
            }.toSet,
            users.map {
              case (
                  username,
                  IntermediateApplicationUser(
                    password,
                    firstName,
                    lastName,
                    attributes,
                    IntermediateRoles(realmRoles, clientRoles)
                  )
                  ) =>
                ApplicationUser(
                  username,
                  password,
                  firstName,
                  lastName,
                  attributes,
                  Roles(
                    realmRoles,
                    clientRoles.map {
                      case (clientId, roles) => ClientRoles(clientId, roles)
                    }.toSet
                  )
                )
            }.toSet,
            clientScopes.map {
              case (scopeName, IntermediateClientScope(intermediateMappers, default)) =>
                ClientScope(
                  scopeName,
                  intermediateMappers.map {
                    case (mapperName, mapper) =>
                      mapper match {
                        case intermediate.UserAttribute(attributeName) => UserAttribute(mapperName, attributeName)
                      }
                  }.toSet,
                  default
                )
            }.toSet
          )
      }.toSet
    )
  }
}
