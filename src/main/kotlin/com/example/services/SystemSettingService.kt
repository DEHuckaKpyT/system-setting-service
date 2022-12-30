package com.example.services

import com.example.models.SystemSetting
import com.example.models.SystemSettings
import com.example.plugins.batchUpsert
import com.example.plugins.executeQuery
import java.time.LocalDateTime.now


/**
 * Created on 29.12.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
class SystemSettingService {

    suspend fun put(key: String, value: String?): SystemSetting = executeQuery {
//        SystemSetting.new(key) {
//            this.value = value
//        }
        SystemSetting.findById(key)
            ?.apply {
                this.value = value
                this.updatedDate = now()
            }
            ?: SystemSetting.new(key) {
                this.value = value
            }
    }

    suspend fun putAll(settings: Set<Pair<String, String?>>) = executeQuery {
        SystemSettings.batchUpsert(settings) { batch, setting ->
            batch[key] = setting.first
            batch[value] = setting.second
            batch[updatedDate] = now()
        }
    }
}