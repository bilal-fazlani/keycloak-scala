package tech.bilal.keycloak.http.client

import io.bullet.borer.{Decoder, Encoder, Json}
import sttp.client.{ByteArrayBody, ResponseAs, asByteArray}
import sttp.model.MediaType

import scala.language.implicitConversions

trait BorerCompat {
  implicit def encode[T: Encoder](value: T): ByteArrayBody =
    ByteArrayBody(Json.encode(value).toByteArray, Some(MediaType.ApplicationJson))

  implicit def as[T: Decoder]: ResponseAs[Either[Array[Byte], T], Nothing] =
    asByteArray.map {
      case Left(value)  => Left(value.getBytes)
      case Right(bytes) => Json.decode(bytes).to[T].valueEither.left.map(_ => bytes)
    }
}
