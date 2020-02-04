package tech.bilal.keycloak.http.client

import sttp.model.StatusCode

sealed trait ApiError

object ApiError {
  case class RequestError(statusCode: Option[StatusCode], error: String) extends ApiError
  case class ParsingError(error: String)                                 extends ApiError
}
