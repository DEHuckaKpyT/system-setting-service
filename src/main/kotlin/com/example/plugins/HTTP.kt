package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureHTTP() {

    routing {
//        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
    }
}
