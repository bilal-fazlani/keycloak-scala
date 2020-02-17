package tech.bilal.keycloak.token.cli

import caseapp.{ExtraName, HelpMessage, Name}

case class Options(
    @HelpMessage("keycloak url. Default: http://localhost:8080")
    @Name("url")
    keycloakUrl: String = "http://localhost:8080",
    @ExtraName("u")
    username: String,
    @ExtraName("p")
    password: String,
    @HelpMessage("realm name. Default: master")
    @ExtraName("r")
    realm: String
)
