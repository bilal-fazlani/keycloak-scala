inThisBuild(
  List(
    scalaVersion := "2.13.1",
    version := {
      sys.env.get("CI") match {
        case Some("true") => version.value
        case _            => "0.1.0-SNAPSHOT"
      }
    },
    organization := "tech.bilal",
    homepage := Some(url("https://github.com/bilal/keycloak-scala")),
    licenses := List(
      "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    developers := List(
      Developer("bilal-fazlani", "Bilal Fazlani", "", url("https://github.com/bilal-fazlani"))
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-Xmacro-settings:materialize-derivations",
      "-encoding",
      "UTF-8",
      "-language:experimental.macros",
      "-feature",
      "-unchecked",
//      "-Xfatal-warnings",
      "-Xlint",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard"
    ),
    parallelExecution in Test in ThisBuild := false
  )
)

lazy val `keycloak-scala` = project
  .in(file("./"))
  .aggregate(
    `keycloak-http-client`,
    `keycloak-data`,
    `embedded-keycloak`,
    `keycloak-data-push-cli`
  )

lazy val `keycloak-http-client` = project
  .in(file("keycloak-http-client"))
  .settings(
    libraryDependencies ++= Seq(
      Sttp.Model.core,
      Sttp.Client.core,
      Sttp.Client.`backend-zio`,
      Borer.`borer-core`,
      Borer.`borer-derivation`
    )
  )

lazy val `keycloak-data` = project
  .in(file("keycloak-data"))
  .dependsOn(`keycloak-http-client`)
  .settings(
    libraryDependencies ++= Seq(
      Libs.ficus,
      Zio.zio,
      Libs.pureConfig,
      Libs.pprint
    )
  )

lazy val `embedded-keycloak` =
  project
    .in(file("embedded-keycloak"))
    .dependsOn(`keycloak-data`)
    .settings(
      libraryDependencies ++= Seq(
        Zio.zio,
        Zio.`zio-process`,
        Sttp.Model.core,
        Sttp.Client.core,
        Sttp.Client.`backend-zio`,
        Sttp.Client.`backend-zio-streaming`,
        Libs.`slf4j-simple` % Test
      )
    )

lazy val `keycloak-data-push-cli` = project
  .in(file("keycloak-data-push-cli"))
  .dependsOn(`keycloak-data`)
  .settings(
    libraryDependencies ++= Seq(
      Libs.`case-app`,
      Libs.`scala-uri`,
      Libs.pprint,
      Libs.`slf4j-simple`
    )
  )
