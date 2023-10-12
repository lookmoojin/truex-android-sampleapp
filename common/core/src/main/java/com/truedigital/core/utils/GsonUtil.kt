package com.truedigital.core.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class GsonUtil private constructor(typeOptions: List<Type>?) {

    private val gson: Gson

    init {
        gson = GsonBuilder()
            .registerTypeAdapterFactory(SafeObjectTypeAdapterFactory(typeOptions))
            .setPrettyPrinting()
            .create()
    }

    companion object {
        @JvmStatic
        fun newInstance(typeOptions: List<Type>? = null): GsonUtil {
            return GsonUtil(typeOptions)
        }
    }

    /**====================================
     * Public method
     * ==================================== */

    fun <T> getValue(jsonStr: String?, type: Type): T? {
        try {
            if (jsonStr !== null) {
                return gson.fromJson<T>(jsonStr, type)
            }
        } catch (e: Exception) {
            // DO NOTHING
        }

        return null
    }

    fun <T> getValue(obj: Any?, type: Type): T? {
        try {
            if (obj !== null) {
                val jsonStr = gson.toJson(obj)

                return gson.fromJson<T>(jsonStr, type)
            }
        } catch (e: Exception) {
            // DO NOTHING
        }

        return null
    }

    /**====================================
     * TypeAdapters
     * ==================================== */

    private class SafeObjectTypeAdapterFactory constructor(val typeOptions: List<Type>?) :
        TypeAdapterFactory {
        override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {

            when (type.rawType) {
                Int::class.java -> {
                    return IntTypeAdapter() as TypeAdapter<T>
                }

                Boolean::class.java -> {
                    return BooleanTypeAdapter() as TypeAdapter<T>
                }

                String::class.java -> {
                    return StringTypeAdapter() as TypeAdapter<T>
                }

                MutableList::class.java -> {
                    val clazz = (type.type as ParameterizedType).actualTypeArguments[0] as Class<T>
                    return ArrayAdapter(gson, clazz) as TypeAdapter<T>?
                }

                else -> {
                    typeOptions?.let {
                        if (it.contains(type.rawType)) {

                            val delegate = gson.getDelegateAdapter(this, type)
                            val elementAdapter = gson.getAdapter(JsonElement::class.java)

                            return ObjectTypeAdapter(delegate, elementAdapter)
                        }
                    }
                }
            }

            return null
        }

        /**
         * ObjectTypeAdapter
         * Validate Custom class if otherwise null given
         */
        private class ObjectTypeAdapter<T>(
            val delegate: TypeAdapter<T>,
            val elementAdapter: TypeAdapter<JsonElement>
        ) : TypeAdapter<T>() {
            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: T?) {
                val tree = delegate.toJsonTree(value)

                elementAdapter.write(out, tree)
            }

            @Throws(IOException::class)
            override fun read(reader: JsonReader): T? {

                val tree = elementAdapter.read(reader)
                if (tree.isJsonObject) {
                    return delegate.fromJsonTree(tree)
                }
                return null
            }
        }

        /**
         * IntTypeAdapter
         * Validate Int if otherwise 0 given
         */
        private class IntTypeAdapter : TypeAdapter<Int>() {

            @Throws(IOException::class)
            override fun write(writer: JsonWriter, value: Int?) {
                value?.let {
                    writer.value(value)
                } ?: kotlin.run {
                    writer.nullValue()
                }
            }

            @Throws(IOException::class)
            override fun read(reader: JsonReader): Int? {
                val peek = reader.peek()

                if (peek != JsonToken.NUMBER) {
                    reader.skipValue()
                    return 0
                }
                return reader.nextInt()
            }
        }

        /**
         * BooleanTypeAdapter
         * Validate Boolean if otherwise null given
         */
        private class BooleanTypeAdapter : TypeAdapter<Boolean>() {

            @Throws(IOException::class)
            override fun write(writer: JsonWriter, value: Boolean?) {
                value?.let {
                    writer.value(value)
                } ?: kotlin.run {
                    writer.nullValue()
                }
            }

            @Throws(IOException::class)
            override fun read(reader: JsonReader): Boolean? {
                val peek = reader.peek()

                if (peek != JsonToken.BOOLEAN) {
                    reader.skipValue()
                    return null
                }
                return reader.nextBoolean()
            }
        }

        /**
         * StringTypeAdapter
         * Validate String if otherwise empty string ("") given
         */
        private class StringTypeAdapter : TypeAdapter<String>() {

            @Throws(IOException::class)
            override fun write(writer: JsonWriter, value: String?) {
                value?.let {
                    writer.value(value)
                } ?: kotlin.run {
                    writer.nullValue()
                }
            }

            @Throws(IOException::class)
            override fun read(reader: JsonReader): String? {
                val peek = reader.peek()

                if (peek != JsonToken.STRING) {
                    reader.skipValue()
                    return ""
                }

                return reader.nextString()
            }
        }

        /**
         * SafeListObjectTypeAdapter
         * Validate String if otherwise null given
         */
        private class ArrayAdapter<T>(val gson: Gson, val adapterclass: Class<T>) :
            TypeAdapter<MutableList<T>>() {

            override fun write(writer: JsonWriter, values: MutableList<T>?) {
                values?.let {
                    val typeAdapter = gson.getAdapter(values.javaClass) as TypeAdapter<Any>

                    return@let typeAdapter.write(writer, values)
                } ?: kotlin.run {
                    writer.nullValue()
                }
            }

            override fun read(reader: JsonReader): MutableList<T>? {

                when (reader.peek()) {
                    JsonToken.BEGIN_OBJECT -> {
                        val list = mutableListOf<T>()
                        val obj = gson.fromJson(reader, adapterclass) as T
                        list.add(obj)

                        return list
                    }

                    JsonToken.BEGIN_ARRAY -> {
                        val list = mutableListOf<T>()

                        reader.beginArray()
                        while (reader.hasNext()) {
                            val obj = gson.fromJson(reader, adapterclass) as T
                            list.add(obj)
                        }
                        reader.endArray()

                        return list
                    }

                    else -> {
                        reader.skipValue()
                    }
                }

                return null
            }
        }
    }
}
