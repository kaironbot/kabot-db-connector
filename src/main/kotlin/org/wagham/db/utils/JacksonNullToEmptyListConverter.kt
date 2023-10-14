package org.wagham.db.utils

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.Converter
import org.wagham.db.models.embed.CraftRequirement

class JacksonNullToEmptyListConverter : Converter<Nothing, List<CraftRequirement>> {
    override fun convert(value: Nothing?): List<CraftRequirement> {
        TODO("Not yet implemented")
    }

    override fun getInputType(typeFactory: TypeFactory?): JavaType {
        TODO("Not yet implemented")
    }

    override fun getOutputType(typeFactory: TypeFactory?): JavaType {
        TODO("Not yet implemented")
    }
}