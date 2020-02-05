package tech.bilal.keycloak.data.cli

import caseapp._
import io.lemonlabs.uri.AbsoluteUrl
import tech.bilal.keycloak.data.KeycloakData
import tech.bilal.keycloak.http.client.KeycloakConnectionInfo
import zio.{DefaultRuntime, Task, UIO}

case object Main extends CommandApp[Command] {
  override def appName: String  = "keycloak data utility"
  override def progName: String = "kc-data"

  def run(cmd: Command, arg: RemainingArgs): Unit = {
    cmd match {
      case Push(file, keycloakUrl, overwrite, verbose) =>
        val defaultRuntime = new DefaultRuntime {}
        val program = (for {
          data <- KeycloakData.fromFile(file)
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
}
