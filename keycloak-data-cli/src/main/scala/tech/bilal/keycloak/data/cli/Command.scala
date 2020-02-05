package tech.bilal.keycloak.data.cli

import caseapp._

sealed trait Command

@CommandName("push")
case class Push(
    @HelpMessage("path of config file")
    @ValueDescription("file")
    @ExtraName("f")
    file: String,
    @HelpMessage("keycloak url")
    @ExtraName("u")
    keycloakUrl: String = "http://localhost:8080",
    @HelpMessage("overwrite existing realms")
    @ExtraName("o")
    overwrite: Boolean = false,
    @HelpMessage("show verbose logs")
    @ExtraName("v")
    verbose: Boolean = false
) extends Command
