package tech.bilal.keycloak.embedded

import sttp.client._
import sttp.client.asynchttpclient.ziostreams._

import java.nio.ByteBuffer

import zio._
import zio.stream._
import scala.concurrent.duration.Duration

class Downloader {
  def download() = {
    //abc
    AsyncHttpClientZioStreamsBackend(new DefaultRuntime {}).flatMap { implicit backend =>
      val response: Task[Response[Either[String, Stream[Throwable, ByteBuffer]]]] =
        basicRequest
          .post(uri"...")
          .response(asStream[Stream[Throwable, ByteBuffer]])
          .readTimeout(Duration.Inf)
          .send()
      for {
        x <- response
        y <- ZIO.fromEither(x.body)
        z = y.foreachManaged(bb => Task.effect())
      } yield ()
      response
    }
  }
}
