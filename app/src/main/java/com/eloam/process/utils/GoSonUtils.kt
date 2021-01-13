// -*- Mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
package com.eloam.process.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import java.lang.reflect.Type

object GoSonUtils {
    const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"
    val localGson = createLocalGoSon()
    private val sRemoteGoSon = createRemoteGoSon()
    private fun createLocalGoSonBuilder(): GsonBuilder {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setLenient()
        gsonBuilder.setDateFormat(DATE_FORMAT)
        return gsonBuilder
    }

    private fun createLocalGoSon(): Gson {
        return createLocalGoSonBuilder().create()
    }

    private fun createRemoteGoSon(): Gson {
        return createLocalGoSonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }

    @Throws(JsonSyntaxException::class)
    fun <T> fromLocalJson(json: String?, clazz: Class<T>?): T? {
        return try {
            localGson.fromJson(json, clazz)
        } catch (e: JsonSyntaxException) {
            null
        }
    }

    fun <T> fromLocalJson(json: String?, typeOfT: Type?): T {
        return localGson.fromJson(json, typeOfT)
    }

    fun toJson(src: Any?): String {
        return localGson.toJson(src)
    }

    fun toRemoteJson(src: Any?): String {
        return sRemoteGoSon.toJson(src)
    }
}