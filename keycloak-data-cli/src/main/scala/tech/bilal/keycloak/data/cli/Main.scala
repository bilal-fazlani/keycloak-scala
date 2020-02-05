package tech.bilal.keycloak.data.cli

import caseapp._
import caseapp.core.app.CaseApp
import io.lemonlabs.uri.AbsoluteUrl
import tech.bilal.keycloak.data.KeycloakData
import tech.bilal.keycloak.http.client.KeycloakConnectionInfo
import zio.{DefaultRuntime, Task, UIO}

object Main extends CaseApp[Options] {
  def run(options: Options, arg: RemainingArgs): Unit = {
    val defaultRuntime = new DefaultRuntime {}
    import options._
    val program = (for {
      data <- KeycloakData.fromFile(options.file)
      _ = if (verbose) {
        println(s"data file: '$file' parsed successfully")
        pprint.pprintln(data)
      }
      url        <- Task.fromTry(AbsoluteUrl.parseTry(keycloakUrl))
      _          = if (verbose) println(s"pushing parsed data to '$url'")
      host       = url.host.value
      port       = url.port.getOrElse(80)
      scheme     = url.scheme
      connection = KeycloakConnectionInfo(scheme, host, port)
      _          <- data.pushTo(connection, overwrite)
      _          = if (verbose) println(s"data pushed successfully")
    } yield 0).tapError(e => Task.effect(scala.Console.err.println(e.toString))).orElse(UIO.succeed(1))

    exit(defaultRuntime.unsafeRun(program))
  }
}
