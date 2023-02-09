package com.bluehabit.budgetku.common

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class ListUsgFileToStringConverter: AttributeConverter<List<Any>, String> {
    override fun convertToDatabaseColumn(attribute: List<Any>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<Any>>() {}.type

        return if (attribute == null) "[]" else gson.toJson(attribute, type)
    }

    override fun convertToEntityAttribute(dbData: String?): List<Any> {
        val gson = Gson()
        val type = object : TypeToken<List<Any>>() {}.type

        return if (dbData == null) listOf() else gson.fromJson(dbData, type)
    }
}