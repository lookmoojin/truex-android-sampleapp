package com.truedigital.features.tuned.data.authentication.model

import android.util.Base64
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import timber.log.Timber

data class AuthenticationToken(
    var refreshToken: String,
    var expiration: Long,
    var accessToken: String?
) {

    val userId: Int?
        get() = accessToken?.let {
            val tokenPayload = it.substring(it.indexOf(".") + 1, it.lastIndexOf("."))
            JsonParser.parseString(
                String(
                    Base64.decode(
                        tokenPayload,
                        Base64.DEFAULT
                    )
                )
            ).asJsonObject["sub"].asInt
        }

    val hasArtistShuffleRight: Boolean
        get() = hasRight("artist:shuffle")

    val hasAlbumShuffleRight: Boolean
        get() = hasRight("album:shuffle")

    val hasCatalogueOfflineRight: Boolean
        get() = hasRight("catalogue:offline")

    val hasSequentialPlaybackRight: Boolean
        get() = hasRight("play:sequential")

    val hasTrackPlayRight: Boolean
        get() = hasRight("track:play")

    val hasPlaylistWriteRight: Boolean
        get() = hasRight("playlist:write")

    private fun hasRight(right: String): Boolean {
        val tokenPayload = getTokenPayload()
        if (tokenPayload != null) {
            val permissionComponents = right.split(":")
            try {
                val scopes = tokenPayload["scope"] as JsonArray?

                val permissionKey = permissionComponents[0]
                val permissionValue = permissionComponents[1]

                scopes?.let {
                    // Scopes can be in the formats as below
                    // key:value
                    // key:value,value,value
                    for (i in 0 until scopes.size()) {
                        val scope = scopes[i].asString
                        val scopeComponents = scope.split(":")
                        val key = scopeComponents[0]
                        val scopeValues = scopeComponents[1].split(",")

                        if (key == permissionKey) {
                            for (scopeValue in scopeValues) {
                                if (scopeValue == permissionValue) {
                                    return true
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                return false
            }
        }

        return false
    }

    private fun getTokenPayload(): JsonObject? {
        accessToken?.let {
            val tokenPayload = it.substring(it.indexOf(".") + 1, it.lastIndexOf("."))
            val decodedPayload = Base64.decode(tokenPayload, Base64.DEFAULT)
            return try {
                JsonParser.parseString(String(decodedPayload)).asJsonObject
            } catch (e: JsonSyntaxException) {
                Timber.e(e)
                null
            }
        }

        return null
    }
}
