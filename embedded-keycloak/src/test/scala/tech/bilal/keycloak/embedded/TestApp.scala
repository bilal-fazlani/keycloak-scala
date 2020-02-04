package tech.bilal.keycloak.embedded

import tech.bilal.keycloak.http.client.KeycloakConnectionInfo
import zio.console.putStrLn
import zio.{Task, UIO, ZIO}

object TestApp extends zio.App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val embeddedKeycloak = new EmbeddedKeycloak(KeycloakConnectionInfo("http", "localhost", 8081))
    embeddedKeycloak.startAndSeedData
      .tap(_ => putStrLn("data feed complete"))
      .flatMap(sh => sh.stopAndClean *> putStrLn("cleaned data"))
      .as(0)
      .tapError {
        case e: RuntimeException => Task.effect(e.printStackTrace())
        case e                   => putStrLn(e.toString)
      }
      .orElse(UIO.succeed(1))
  }
}
