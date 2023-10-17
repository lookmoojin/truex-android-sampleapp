package com.truedigital.features.tuned.service.music.model

import com.truedigital.features.tuned.data.station.model.Stakkar
import com.truedigital.features.tuned.service.util.PlayQueue

class StakkarQueue(initialItems: Collection<Stakkar>, val attachedTrackId: Int? = null) :
    PlayQueue<Stakkar>(initialItems)
