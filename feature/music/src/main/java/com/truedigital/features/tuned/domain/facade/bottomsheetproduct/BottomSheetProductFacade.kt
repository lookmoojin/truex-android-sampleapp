package com.truedigital.features.tuned.domain.facade.bottomsheetproduct

import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import io.reactivex.Single

interface BottomSheetProductFacade {
    fun getProduct(id: Int, type: ProductPickerType?): Single<Product>
    fun addToCollection(id: Int, type: ProductPickerType?): Single<Any>
    fun removeFromCollection(id: Int, type: ProductPickerType?): Single<Any>
    fun isInCollection(id: Int, type: ProductPickerType?): Single<Boolean>
    fun getHasArtistAndSimilarStation(artistId: Int): Single<Boolean>
    fun checkPlaylistType(creatorId: Int): Single<ProductPickerType>
    fun getTracksForProduct(id: Int, type: ProductPickerType?): Single<List<Track>>
    fun clearVotes(
        productId: Int,
        productPickerType: ProductPickerType?,
        voteType: String = "All"
    ): Single<Any>
}
