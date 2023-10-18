package com.truedigital.features.tuned.injection.component

import android.support.v4.media.session.MediaSessionCompat
import com.truedigital.features.tuned.application.configuration.Configuration
import dagger.Subcomponent

@Subcomponent
interface ApplicationComponent {
    fun getMediaSession(): MediaSessionCompat
    fun getConfiguration(): Configuration
}
