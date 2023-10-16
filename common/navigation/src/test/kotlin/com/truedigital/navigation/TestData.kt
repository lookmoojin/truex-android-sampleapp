package com.truedigital.navigation

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.truedigital.core.data.ShelfSkeleton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

object TestData {

    object Exception {
        val http404 = HttpException(
            Response.error<Any>(
                404,
                "".toResponseBody("application/json".toMediaTypeOrNull())
            )
        )
    }

    fun getShelfSkeletonJsonArray(count: Int): JsonArray {
        val jsonArray = JsonArray()
        for (index in 0 until count) {
            jsonArray.add(
                JsonObject().apply {
                    add(
                        "header",
                        JsonObject().apply {
                            addProperty("vType", "test")
                            addProperty("navigate", "")
                        }
                    )
                    addProperty("theme", "white")
                    addProperty("shelfId", "$index")
                }
            )
        }
        return jsonArray
    }

    fun getShelfSkeletonList(count: Int): List<ShelfSkeleton> {
        val result = mutableListOf<ShelfSkeleton>()
        if (count <= 0) return emptyList()
        for (index in 0 until count) {
            result.add(
                ShelfSkeleton(
                    json = JsonObject(),
                    index = index
                )
            )
        }
        return result
    }
}
