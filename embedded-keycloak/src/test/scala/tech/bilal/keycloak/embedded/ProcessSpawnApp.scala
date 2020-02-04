package tech.bilal.keycloak.embedded

import zio.console.putStrLn
import zio.process.Command
import zio.{Task, UIO, ZIO}

object ProcessSpawnApp extends zio.App {
  private val command = Command(
    "cat",
    "ProcessSpawnApp.scala"
  )

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    (for {
      p     <- command.run
      lines <- p.lines
      _     <- lines.map(putStrLn).reduce(_ *> _)
    } yield 0)
      .tapError(e => Task.effect(e.printStackTrace()))
      .orElse(UIO.succeed(1))
}
