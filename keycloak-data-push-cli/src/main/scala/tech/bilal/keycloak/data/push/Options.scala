package tech.bilal.keycloak.data.push

import caseapp._

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
    overwrite: Boolean = false,
    @HelpMessage("show verbose logs")
    @ExtraName("v")
    verbose: Boolean = false
)
