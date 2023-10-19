package com.truedigital.navigation.di.multi

import com.truedigital.navigation.di.multi.trueclouds.v2.NavigationTrueCloudsV2DeeplinkModule
import com.truedigital.navigation.di.multi.trueclouds.v3.NavigationTrueCloudsV3DeeplinkModule
import dagger.Module

@Module(
    includes = [
        NavigationTrueCloudsV2DeeplinkModule::class,
        NavigationTrueCloudsV3DeeplinkModule::class
    ]
)
object NavigationMultiBindingDeepLinksModule
