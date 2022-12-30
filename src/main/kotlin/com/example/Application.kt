package com.example

import com.example.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>): Unit =
    io.ktor.server.tomcat.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureRouting()
    configureDatabase()
    configureStatusPages()
}
