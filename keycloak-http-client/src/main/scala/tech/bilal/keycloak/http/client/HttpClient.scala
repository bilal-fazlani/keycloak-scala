package tech.bilal.keycloak.http.client

import io.bullet.borer.{Decoder, Encoder, Utf8}
import ApiError._
import sttp.client.asynchttpclient.WebSocketHandler
import sttp.client.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.client.{Request, Response, SttpBackend, basicRequest}
import sttp.model.{StatusCode, Uri}
import tech.bilal.keycloak.http.client.ApiError.{ParsingError, RequestError}
import tech.bilal.keycloak.http.client.dto.res.TokenResponse
import zio.{IO, Task}

trait HttpClient extends BorerCompat {
  protected val backend: Task[SttpBackend[Task, Nothing, WebSocketHandler]] = AsyncHttpClientZioBackend()

  protected def sendAndDecode[A](req: Request[Either[Array[Byte], A], Nothing]): IO[ApiError, A] =
    send[Array[Byte], A](req).flatMap(res => body(res))

  protected def sendAndGetStatus[A](req: Request[Either[String, String], Nothing]): IO[RequestError, StatusCode] =
    send(req).map(_.code)

  protected def sendAndGetLocation[A](req: Request[Either[String, String], Nothing]): IO[ApiError, String] =
    send(req)
      .map(_.headers.find(_.name.toLowerCase == "location"))
      .flatMap(
        IO.fromOption(_)
          .mapError(_ => ParsingError(s"could not find location in headers for: ${req.method.method.toUpperCase} ${req.uri}"))
      )
      .map(_.value.split("/").last)

  protected def get[T: Decoder](uri: Uri)(implicit token: TokenResponse): Request[Either[Array[Byte], T], Nothing] =
    basicRequest
      .get(uri)
      .auth
      .bearer(token.access_token)
      .response(as[T])

  protected def post[T: Encoder](
      body: T
  )(uri: Uri)(implicit token: TokenResponse): Request[Either[String, String], Nothing] =
    basicRequest
      .post(uri)
      .auth
      .bearer(token.access_token)
      .body(body)

  protected def delete[T: Encoder](
      body: T
  )(uri: Uri)(implicit token: TokenResponse): Request[Either[String, String], Nothing] =
    basicRequest
      .delete(uri)
      .auth
      .bearer(token.access_token)
      .body(body)

  protected def put[T: Encoder](
      body: T
  )(uri: Uri)(implicit token: TokenResponse): Request[Either[String, String], Nothing] =
    basicRequest
      .put(uri)
      .auth
      .bearer(token.access_token)
      .body(body)

  protected def send[E, A](req: Request[Either[E, A], Nothing]): IO[RequestError, Response[Either[E, A]]] =
    for {
      back <- backend.mapError(e => RequestError(None, e.getMessage))
      res <- req
              .send()(back, <:<.refl)
              .mapError(e => RequestError(None, e.getMessage))
              .reject {
                case res if !res.code.isSuccess =>
                  RequestError(
                    Some(res.code),
                    s"${req.method.method.toUpperCase} ${req.uri} request failed with status code: ${res.code.code}"
                  )
              }
    } yield res

  protected def body[_, A](res: Response[Either[Array[Byte], A]]): IO[ParsingError, A] =
    IO.fromEither(res.body)
      .mapError(bytes => ParsingError(s"could not decode response: ${Utf8.decode(bytes).mkString}"))
}
