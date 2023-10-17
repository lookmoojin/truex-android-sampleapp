package com.truedigital.features.tuned.service.music.model

import com.truedigital.features.tuned.data.ad.model.Ad
import com.truedigital.features.tuned.service.util.PlayQueue

class AdQueue(initialItems: Collection<Ad>, val attachedTrackId: Int? = null) :
    PlayQueue<Ad>(initialItems)
