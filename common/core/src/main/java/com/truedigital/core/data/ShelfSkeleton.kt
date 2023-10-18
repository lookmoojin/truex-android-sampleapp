package com.truedigital.core.data

import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.getDefaultNightMode
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlin.math.abs

data class ShelfSkeleton(
    val json: JsonObject,
    val parentShelfId: String? = null,
    var index: Int = -1
) {
    var shelfIndex: Int = -1
    var originalShelfIndex: Int = -1
    var state: LoadingState = LoadingState.LOADING
    var data: Any? = null
    var extraEventData: HashMap<String, Any>? = null
    var timeStamp: Long = 0

    val adsId: String?
        get() {
            return try {
                items?.firstOrNull()?.asJsonObject
                    ?.get("data")?.asJsonObject
                    ?.get("setting")?.asJsonObject
                    ?.get("ads_id")?.asString
            } catch (e: Exception) {
                null
            }
        }
    val adsSize: String?
        get() {
            return try {
                items?.firstOrNull()?.asJsonObject
                    ?.get("data")?.asJsonObject
                    ?.get("setting")?.asJsonObject
                    ?.get("size")?.asString
            } catch (e: Exception) {
                null
            }
        }

    val adsSizeTablet: String?
        get() {
            return try {
                items?.firstOrNull()?.asJsonObject
                    ?.get("data")?.asJsonObject
                    ?.get("setting")?.asJsonObject
                    ?.get("size_tablet")?.asString
            } catch (e: Exception) {
                null
            }
        }
    val componentSetting: JsonObject?
        get() {
            return runCatching {
                items?.get(0)?.asJsonObject
                    ?.get("data")?.asJsonObject
                    ?.get("setting")?.asJsonObject
            }.getOrNull()
        }
    val componentName: String?
        get() {
            return runCatching {
                componentSetting?.get("component_name")?.asString
            }.getOrNull()
        }

    val componentViewType: String?
        get() {
            return runCatching { items?.get(0)?.asJsonObject?.get("viewType")?.asString }.getOrNull()
        }

    val items: JsonArray?
        get() {
            return try {
                json.get("items").asJsonArray
            } catch (e: Exception) {
                null
            }
        }

    val vType: String?
        get() {
            return try {
                json.get("vType").asString
            } catch (e: Exception) {
                null
            }
        }
    val shelfId: String?
        get() = try {
            when {
                json.get("shelfId") != null -> {
                    json.get("shelfId").asString
                }

                parentShelfId != null -> {
                    "$parentShelfId:$vType"
                }

                else -> {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }

    val shelfName: String?
        get() = try {
            json.getAsJsonObject("header").getAsJsonObject("data").get("title").asString
        } catch (e: Exception) {
            null
        }

    val shelfNameTitle: String?
        get() = try {
            json.get("shelfName").asString
        } catch (e: Exception) {
            null
        }

    val isTopNav: Boolean
        get() = try {
            items?.get(0)?.asJsonObject?.get("viewType")?.asString == CommonViewModel.TopNavigation.VIEW_TYPE
        } catch (e: Exception) {
            false
        }

    val isHeaderFooter: Boolean
        get() = try {
            vType?.contains("header") == true ||
                vType?.contains("footer") == true
        } catch (e: Exception) {
            false
        }

    val isInLineBanner: Boolean
        get() = try {
            items?.get(0)?.asJsonObject?.get("data")?.asJsonObject
                ?.get("setting")?.asJsonObject
                ?.get("component_name")?.asString == CommonViewModel.ShelfComponent.COMPONENT_INLINE_BANNER
        } catch (e: Exception) {
            false
        }

    // For the get getItemViewType at adapter
    fun getLoadingStableId(position: Int): Int {
        return abs("$shelfId-loading$position".hashCode())
    }

    fun getErrorStableId(position: Int): Int {
        return abs("$shelfId-error$position".hashCode())
    }

    fun getStableId(position: Int): Int {
        // Error case
        if (this.isContentError) {
            return abs(this.getErrorStableId(position))
        }

        // Loading case
        if (this.isContentFetching) {
            return abs(this.getLoadingStableId(position))
        }

        return if (this.shelfId != null) {
            abs("${this.shelfId}$timeStamp".hashCode())
        } else {
            -1
        }
    }
    // get(if (shelfId.isNullOrBlank()) loadingStableId.hashCode() else shelfId?.hashCode() ?: -1

    val isContentReady: Boolean
        get() = state == LoadingState.LOADED

    val isContentFetching: Boolean
        get() = state == LoadingState.LOADING

    val isContentError: Boolean
        get() = state == LoadingState.ERROR

    val moduleName: String?
        get() = vType?.split("/")?.firstOrNull()
    val version: String?
        get() = vType?.split("/")?.getOrNull(1)
    val viewType: String?
        get() = vType?.split("/")?.getOrNull(2)

    val typeCallApi: String
        get() {
            return try {
                json.get("type_call_api").asString
            } catch (e: Exception) {
                ""
            }
        }

    val placementId: String
        get() {
            return try {
                json.get("placement_id").asString
            } catch (e: Exception) {
                ""
            }
        }

    val limit: String
        get() {
            return try {
                json.get("limit").asString
            } catch (e: Exception) {
                ""
            }
        }

    val reRankShelf: Boolean
        get() {
            return try {
                json.get("rerank_shelf").asBoolean
            } catch (e: Exception) {
                false
            }
        }
    val shelfCustomType: String
        get() {
            return try {
                json.get("shelf_custom_type").asString
            } catch (e: Exception) {
                ""
            }
        }

    val gradientStart: String
        get() {
            return try {
                json.get("gradient_color_start").asString
            } catch (e: Exception) {
                ""
            }
        }

    val gradientEnd: String
        get() {
            return try {
                json.get("gradient_color_end").asString
            } catch (e: Exception) {
                ""
            }
        }

    val asset: Asset
        get() {
            return try {
                when (json.get("asset").asString) {
                    "movie" -> Asset.MOVIE
                    else -> Asset.NONE
                }
            } catch (e: Exception) {
                Asset.NONE
            }
        }

    val isDarkTheme: Boolean
        get() {
            return runCatching {
                when (json.get("theme").asString) {
                    "white", "light" -> false
                    "black", "dark" -> true
                    else -> getDefaultNightMode() == MODE_NIGHT_YES
                }
            }.getOrDefault(getDefaultNightMode() == MODE_NIGHT_YES)
        }

    val isFetchContent: Boolean
        get() = typeCallApi != "" || reRankShelf

    var loadedFrom: LoadedFrom = LoadedFrom.CDN
}

enum class Asset {
    MOVIE, NONE
}

enum class LoadedFrom {
    API, CDN
}

enum class LoadingState {
    LOADING,
    LOADED,
    ERROR
}
