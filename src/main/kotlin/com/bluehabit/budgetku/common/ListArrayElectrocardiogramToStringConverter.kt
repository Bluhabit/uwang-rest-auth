package com.bluehabit.budgetku.common

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class ListArrayElectrocardiogramToStringConverter :AttributeConverter<List<List<Int>>,String> {
    override fun convertToDatabaseColumn(attribute: List<List<Int>>?): String {
        val gson = Gson()
        val type = object : TypeToken<List<List<Int>>>() {}.type

        return  if(attribute == null) "[]" else gson.toJson(attribute,type)
    }

    override fun convertToEntityAttribute(dbData: String?): List<List<Int>> {
        val gson = Gson()
        val type = object : TypeToken<List<List<Int>>>() {}.type
        return if(dbData == null) listOf() else gson.fromJson(dbData,type)
    }
}