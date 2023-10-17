package com.truedigital.features.music.data.landing.repository

import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import javax.inject.Inject

interface CacheMusicLandingRepository {
    fun savePathApiForYouShelf(path: String?)
    fun saveMusicShelfData(baseShelfId: String, shelves: MutableList<MusicForYouShelfModel>)
    fun saveFAShelf(baseShelfId: String, shelfIndex: Int)
    fun loadPathApiForYouShelf(): String?
    fun loadMusicShelfData(baseShelfId: String): Pair<String, MutableList<MusicForYouShelfModel>>?
    fun getFAShelfList(baseShelfId: String): MutableSet<Int>
    fun clearCache()
    fun clearCacheIfCountryOrLanguageChange(countryCode: String, languageCode: String)
}

class CacheMusicLandingRepositoryImpl @Inject constructor() : CacheMusicLandingRepository {

    private var pathApiForYouShelf: String? = null
    private var musicShelfData: ArrayList<Pair<String, MutableList<MusicForYouShelfModel>>> =
        arrayListOf()
    private var faShelfList: ArrayList<Pair<String, MutableSet<Int>>> = arrayListOf()
    private var lastAppCountryCode: String? = null
    private var lastAppLanguageCode: String? = null

    override fun savePathApiForYouShelf(path: String?) {
        pathApiForYouShelf = path
    }

    override fun saveMusicShelfData(
        baseShelfId: String,
        shelves: MutableList<MusicForYouShelfModel>
    ) {
        val shelfData = Pair(baseShelfId, shelves)
        val shelfIndex = musicShelfData.indexOfFirst {
            it.first == baseShelfId
        }

        if (shelfIndex != MusicConstant.Index.NOT_FOUND_INDEX) {
            musicShelfData[shelfIndex] = shelfData
        } else {
            musicShelfData.add(shelfData)
        }
    }

    override fun saveFAShelf(baseShelfId: String, shelfIndex: Int) {
        val faShelfIndex = faShelfList.indexOfFirst {
            it.first == baseShelfId
        }

        if (faShelfIndex != MusicConstant.Index.NOT_FOUND_INDEX) {
            faShelfList[faShelfIndex].second.add(shelfIndex)
        } else {
            faShelfList.add(Pair(baseShelfId, mutableSetOf(shelfIndex)))
        }
    }

    override fun loadPathApiForYouShelf(): String? = pathApiForYouShelf

    override fun loadMusicShelfData(baseShelfId: String): Pair<String, MutableList<MusicForYouShelfModel>>? {
        return musicShelfData.firstOrNull {
            it.first == baseShelfId
        }
    }

    override fun getFAShelfList(baseShelfId: String): MutableSet<Int> {
        return faShelfList.find { it.first == baseShelfId }?.second ?: mutableSetOf()
    }

    override fun clearCache() {
        musicShelfData.clear()
    }

    override fun clearCacheIfCountryOrLanguageChange(countryCode: String, languageCode: String) {
        if (lastAppCountryCode != countryCode || lastAppLanguageCode != languageCode) {
            faShelfList.clear()
            lastAppCountryCode = countryCode
            lastAppLanguageCode = languageCode
        }
    }
}
