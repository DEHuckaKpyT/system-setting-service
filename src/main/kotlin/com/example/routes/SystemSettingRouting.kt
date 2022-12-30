package com.example.routes

import com.example.converters.SystemSettingConverter
import com.example.routes.dto.BatchPutSystemSettingDto
import com.example.routes.dto.PutSystemSettingDto
import com.example.services.SystemSettingService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
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

        }

        get("/get/batch") {

        }
    }
}