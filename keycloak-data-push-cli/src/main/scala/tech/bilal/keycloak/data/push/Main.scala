package tech.bilal.keycloak.data.push

import caseapp._
import caseapp.core.app.CaseApp
import io.lemonlabs.uri.AbsoluteUrl
import tech.bilal.keycloak.data.KeycloakData
import tech.bilal.keycloak.http.client.KeycloakConnectionInfo
import zio.{DefaultRuntime, Task, UIO}
import zio.console._

object Main extends CaseApp[Options] {
  def run(options: Options, arg: RemainingArgs): Unit = {
    val defaultRuntime = new DefaultRuntime {}
    import options._
    val program = (for {
      data   <- KeycloakData.fromFile(options.file)
      _      <- putStrLn(data.toString)
      url    <- Task.fromTry(AbsoluteUrl.parseTry(keycloakUrl))
      host   = url.host
      port   = url.port.getOrElse(80)
      scheme = url.scheme
      _      <- putStrLn(s"$scheme://$host:$port/auth")
//      connection = KeycloakConnectionInfo(scheme, host.value, port)
//      _      <- data.pushTo(connection, overwrite)
    } yield 0).tapError(e => Task.effect(scala.Console.err.println(e.toString))).orElse(UIO.succeed(1))
    exit(defaultRuntime.unsafeRun(program))
  }
}

@AppName("keycloak data importer")
@ProgName("kc-import")
case class Options(
    @HelpMessage("path of config file")
    @ValueDescription("file")
    @ExtraName("f")
    file: String,
    @HelpMessage("keycloak url")
    @ExtraName("u")
    keycloakUrl: String = "http://localhost:8080",
    @HelpMessage("overwrite existing realms")
    @ExtraName("o")
    overwrite: Boolean = false
)
