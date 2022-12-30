package com.example.services

import com.example.models.SystemSetting
import com.example.models.SystemSettings
import com.example.plugins.batchUpsert
import com.example.plugins.executeQuery
import com.example.plugins.upsert
import org.jetbrains.exposed.sql.select
import java.time.LocalDateTime.now


/**
 * Created on 29.12.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
class SystemSettingService {

    suspend fun put(key: String, value: String?) = executeQuery {
        SystemSettings.upsert {
            it[SystemSettings.key] = key
            it[SystemSettings.value] = value
        }
    }

    suspend fun putAll(settings: Set<Pair<String, String?>>) = executeQuery {
        SystemSettings.batchUpsert(settings) { batch, (key, value) ->
            batch[SystemSettings.key] = key
            batch[SystemSettings.value] = value
            batch[SystemSettings.updatedDate] = now()
        }
    }

    suspend fun getValue(key: String): String? = executeQuery {
        SystemSetting[key].value
    }

    suspend fun getValues(keys: Set<String>): Map<String, String?> = executeQuery {
        SystemSettings.slice(SystemSettings.key, SystemSettings.value).select() {
            SystemSettings.key inList keys
        }.associate {
            it[SystemSettings.key] to it[SystemSettings.value]
        }.also {
            it.keys + keys
        }
    }
}