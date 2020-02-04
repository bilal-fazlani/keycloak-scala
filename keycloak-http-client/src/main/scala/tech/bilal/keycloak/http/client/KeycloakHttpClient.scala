package tech.bilal.keycloak.http.client

import sttp.client._
import zio.{IO, Task, ZIO}
import ApiError._
import tech.bilal.keycloak.http.client.dto.res.TokenResponse

class KeycloakHttpClient(keycloakConnectionInfo: KeycloakConnectionInfo) extends HttpClient {
  import keycloakConnectionInfo._

  def getAccessToken(username: String, password: String, realm: String): IO[ApiError, TokenResponse] =
    for {
      back <- backend.mapError(e => RequestError(None, e.getMessage))
      req = basicRequest
        .response(as[TokenResponse])
        .body(
          Map(
            "grant_type" -> "password",
            "client_id"  -> "admin-cli",
            "username"   -> username,
            "password"   -> password
          )
        )
        .post(uri"$baseUrl/realms/$realm/protocol/openid-connect/token")
      res <- req
              .send()(back, <:<.refl)
              .mapError(e => RequestError(None, e.getMessage))
              .reject {
                case x if !x.code.isSuccess =>
                  RequestError(Some(x.code), s"'getAccessToken' request failed with status code ${x.code}")
              }
      body <- IO.fromEither(res.body).mapError(_ => ParsingError("could not decode body"))
    } yield body

  def createAdminUser(username: String, password: String): IO[ApiError, Unit] =
    for {
      back <- backend.mapError(e => RequestError(None, e.getMessage))
      req  = basicRequest.get(baseUrl)
      res1 <- req
               .send()(back, <:<.refl)
               .mapError(e => RequestError(None, e.getMessage))
               .reject {
                 case x if !x.code.isSuccess =>
                   RequestError(Some(x.code), s"'createAdminUser' GET request failed with status code ${x.code}")
               }
      cookies <- Task.effect(res1.cookies).mapError(e => RequestError(Some(res1.code), e.getMessage))
      stateCheckerValue <- ZIO
                            .fromOption(res1.cookies.find(_.name == "WELCOME_STATE_CHECKER"))
                            .map(_.value)
                            .mapError(_ => RequestError(Some(res1.code), "could not find required cookie"))
      _ <- basicRequest
            .post(baseUrl)
            .cookies(cookies)
            .body(
              Map(
                "username"             -> username,
                "password"             -> password,
                "passwordConfirmation" -> password,
                "stateChecker"         -> stateCheckerValue
              )
            )
            .send()(back, <:<.refl)
            .mapError(e => RequestError(None, e.getMessage))
            .reject {
              case x if !x.code.isSuccess =>
                RequestError(Some(x.code), s"'createAdminUser' POST request failed with status code ${x.code}")
            }

    } yield ()
}
