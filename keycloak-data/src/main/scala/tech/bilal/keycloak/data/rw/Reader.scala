package tech.bilal.keycloak.data.rw

import com.typesafe.config.ConfigFactory
import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.error.{CannotParse, CannotReadResource, ConfigReaderFailures}
import tech.bilal.keycloak.data.KeycloakData
import tech.bilal.keycloak.data.intermediate.IntermediateKeycloakData
import zio.{IO, Task}
import pureconfig.generic.auto._

class Reader {
  private implicit val reader: ConfigReader[KeycloakData] =
    ConfigReader[IntermediateKeycloakData].map(_.toKeycloakData)

  def readConfig: IO[ConfigReaderFailures, KeycloakData] =
    for {
      config <- Task
                 .effect(ConfigFactory.load())
                 .mapError(e => ConfigReaderFailures(CannotReadResource(e.getMessage, Some(e))))
      embeddedKeycloakConfig <- Task
                                 .effect(config.getConfig("keycloak-data"))
                                 .mapError(e => ConfigReaderFailures(CannotParse(e.getMessage, None)))
      parsed <- IO.fromEither(ConfigSource.fromConfig(embeddedKeycloakConfig).load[KeycloakData])
    } yield parsed

  def readFile(filePath: String): IO[ConfigReaderFailures, KeycloakData] = {
    case class ConfigFile(keycloakData: KeycloakData)
    for {
      parsed <- IO.fromEither(ConfigSource.file(filePath).load[ConfigFile])
      data   = parsed.keycloakData
    } yield data
  }
}
