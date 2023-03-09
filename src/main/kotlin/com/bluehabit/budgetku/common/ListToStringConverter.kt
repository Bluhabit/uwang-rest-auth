/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku.common

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class ListToStringConverter : AttributeConverter<List<Any>, String> {
    override fun convertToDatabaseColumn(attribute: List<Any>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<Any>>() {}.type

        return  if(attribute == null) "[]" else gson.toJson(attribute,type)
    }

    override fun convertToEntityAttribute(dbData: String?): List<Any> {
        val gson = Gson()
        val type = object : TypeToken<List<Any>>() {}.type
        return if(dbData == null) listOf() else gson.fromJson(dbData,type)
    }
}