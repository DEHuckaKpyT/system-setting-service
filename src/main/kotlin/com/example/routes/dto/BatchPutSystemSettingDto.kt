package com.example.routes.dto


/**
 * Created on 30.12.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
data class BatchPutSystemSettingDto(

    val settings: Set<PutSystemSettingDto>
)
