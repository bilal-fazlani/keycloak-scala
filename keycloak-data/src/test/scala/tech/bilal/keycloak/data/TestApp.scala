package tech.bilal.keycloak.data

import zio.console._
import zio.{IO, Task, ZIO}

object ConfigTestApp extends zio.App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    (for {
      data <- KeycloakData.fromConfig()
      _    <- Task.effect(pprint.pprintln(data))
    } yield 0).orElse(ZIO.succeed(1))
}

object FileTestApp extends zio.App {
  def argFile(args: List[String]): IO[String, String] =
    for {
      firstArg <- IO.fromOption(args.headOption).orElse(IO.fail("file argument missing"))
    } yield firstArg

  def staticResourceFile(filename: String): Task[String] =
    for {
      file <- Task.effect(getClass.getResource(s"/$filename"))
      path <- Task.effect(file.getPath)
    } yield path

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    (for {
      path <- argFile(args).orElse(staticResourceFile("application.conf"))
      data <- KeycloakData.fromFile(path)
      _    <- Task.effect(pprint.pprintln(data))
    } yield 0)
      .tapError(e => putStrLn(e.toString))
      .orElse(ZIO.succeed(1))
}
