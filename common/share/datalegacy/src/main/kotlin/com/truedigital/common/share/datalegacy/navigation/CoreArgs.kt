package com.truedigital.common.share.datalegacy.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import com.truedigital.common.share.datalegacy.domain.discover.model.shelf.BaseShelfModel
import com.truedigital.common.share.datalegacy.domain.discover.model.shelf.ShelfModel
import icepick.State

@MainThread
fun Fragment.navArguments() = NavArgsLazy {
    arguments ?: throw IllegalStateException("Fragment $this has null arguments")
}

class NavArgsLazy(
    private val argumentProducer: () -> Bundle
) : Lazy<CoreArgs> {
    private var cached: CoreArgs? = null

    override val value: CoreArgs
        get() {
            cached = CoreArgs.fromBundle(argumentProducer())
            return cached as CoreArgs
        }

    override fun isInitialized() = cached != null
}

class CoreArgs(
    @State val baseShelfModel: BaseShelfModel? = null,
    @State val shelfModel: ShelfModel? = null,
    @State val queryMap: HashMap<String, String>? = null,
    @State val imageTransitionName: String? = null
) {

    companion object {
        fun fromBundle(bundle: Bundle): CoreArgs {
            val baseShelfModel = bundle.getParcelable<BaseShelfModel>("baseShelfModel") ?: null
            val shelfModel = bundle.getParcelable<ShelfModel>("shelfModel") ?: null
            val queryMap = bundle.getSerializable("queryMap") as? HashMap<String, String> ?: null
            val imageTransitionName = bundle.getString("imageTransitionName") ?: null

            return CoreArgs(baseShelfModel, shelfModel, queryMap, imageTransitionName)
        }
    }

    fun toBundle(): Bundle {
        return Bundle().apply {
            if (baseShelfModel != null) {
                if (Parcelable::class.java.isAssignableFrom(BaseShelfModel::class.java)) {
                    putParcelable("baseShelfModel", baseShelfModel as Parcelable)
                } else {
                    throw UnsupportedOperationException(BaseShelfModel::class.java.name + " must be BaseShelfModel.")
                }
            }

            if (shelfModel != null) {
                if (Parcelable::class.java.isAssignableFrom(ShelfModel::class.java)) {
                    putParcelable("shelfModel", shelfModel as Parcelable)
                } else {
                    throw UnsupportedOperationException(ShelfModel::class.java.name + " must be ShelfModel.")
                }
            }

            if (queryMap != null) {
                putSerializable("queryMap", queryMap)
            }

            if (imageTransitionName != null) {
                putString("imageTransitionName", imageTransitionName)
            }
        }
    }
}
