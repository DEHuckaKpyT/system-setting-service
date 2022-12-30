package com.example.converters

import com.example.routes.dto.PutSystemSettingDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings


/**
 * Created on 29.12.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
@Mapper
interface SystemSettingConverter {

    @Mappings(
        Mapping(source = "key", target = "first"),
        Mapping(source = "value", target = "second")
    )
    fun toArgument(dto: PutSystemSettingDto): Pair<String, String?>

    fun toArgument(dto: Set<PutSystemSettingDto>): Set<Pair<String, String?>>
}