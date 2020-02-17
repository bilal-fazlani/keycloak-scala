import sbt._

object Libs {
  lazy val pureConfig     = "com.github.pureconfig"      %% "pureconfig"  % "0.12.2"
  lazy val `slf4j-simple` = "org.slf4j"                  % "slf4j-simple" % "2.0.0-alpha1"
  lazy val `case-app`     = "com.github.alexarchambault" %% "case-app"    % "2.0.0-M13"
  lazy val `scala-uri`    = "io.lemonlabs"               %% "scala-uri"   % "2.0.0"
  lazy val pprint         = "com.lihaoyi"                %% "pprint"      % "0.5.9"
  lazy val `jwt-core`     = "com.pauldijou"              %% "jwt-core"    % "4.2.0"
}

object Borer {
  private val Version = "1.4.0"
  private val Org     = "io.bullet"

  lazy val `borer-core`       = Org %% "borer-core"       % Version
  lazy val `borer-derivation` = Org %% "borer-derivation" % Version
}

object Sttp {
  object Client {
    private val Org                  = "com.softwaremill.sttp.client"
    private val Version              = "2.0.0-RC10"
    lazy val core                    = Org %% "core" % Version
    lazy val `backend-zio`           = Org %% "async-http-client-backend-zio" % Version
    lazy val `backend-zio-streaming` = Org %% "async-http-client-backend-zio-streams" % Version
  }
  object Model {
    private val Org     = "com.softwaremill.sttp.model"
    private val Version = "1.0.0-RC7"
    lazy val core       = Org %% "core" % Version
  }
}

object Zio {
  private val Org        = "dev.zio"
  lazy val zio           = Org %% "zio" % "1.0.0-RC17"
  lazy val `zio-process` = Org %% "zio-process" % "0.0.1"
}
