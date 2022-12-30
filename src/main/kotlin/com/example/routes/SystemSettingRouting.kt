package com.example.routes

import com.example.converters.SystemSettingConverter
import com.example.routes.dto.BatchPutSystemSettingDto
import com.example.routes.dto.PutSystemSettingDto
import com.example.services.SystemSettingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.mapstruct.factory.Mappers


/**
 * Created on 29.12.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Route.systemSettingRouting() {

    val systemSettingService = SystemSettingService()
    val systemSettingConverter = Mappers.getMapper(SystemSettingConverter::class.java)

    route("/system-setting") {
        post("/put") {
            call.receive<PutSystemSettingDto>()
                .let { systemSettingConverter.toArgument(it) }
                .let { (key, value) -> systemSettingService.put(key, value) }

            call.response.status(HttpStatusCode.OK)
        }

        post("/put/batch") {
            call.receive<BatchPutSystemSettingDto>()
                .let { systemSettingConverter.toArgument(it.settings) }
                .let { systemSettingService.putAll(it) }

            call.response.status(HttpStatusCode.OK)
        }

        get("/get") {
            call.parameters.getOrFail("key")
                .let { systemSettingService.getValue(it) }
                .let { call.respond(mapOf("value" to it)) }
        }

        get("/get/batch") {
            call.parameters.getOrFail<Set<String>>("keys")
                .let { systemSettingService.getValues(it) }
                .let { call.respond(it) }
        }
    }
}