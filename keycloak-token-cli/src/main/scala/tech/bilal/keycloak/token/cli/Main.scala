package tech.bilal.keycloak.token.cli

import caseapp.core.RemainingArgs
import caseapp.core.app.CaseApp
import io.lemonlabs.uri.AbsoluteUrl
import pdi.jwt.Jwt
import tech.bilal.keycloak.http.client.{KeycloakConnectionInfo, RealmClient}
import zio.console._
import zio.{DefaultRuntime, ZIO}

object Main extends CaseApp[Options] {
  override def run(options: Options, remainingArgs: RemainingArgs): Unit = {
    //app
    val url = AbsoluteUrl.parse(options.keycloakUrl)
    val realmClient = new RealmClient(
      KeycloakConnectionInfo(scheme = url.scheme, host = url.host.value, port = url.port.getOrElse(80)),
      options.realm
    )
    val program = for {
      token      <- realmClient.getAccessToken(options.username, options.password, options.realm)
      claim      <- ZIO.fromTry(Jwt.decode(token.access_token))
      expiration = claim.expiration
      _          <- putStr(claim.content)
    } yield ()

    new DefaultRuntime {}.unsafeRun(program)
  }
}
