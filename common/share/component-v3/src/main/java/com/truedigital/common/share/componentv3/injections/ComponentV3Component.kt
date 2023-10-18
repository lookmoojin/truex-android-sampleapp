package com.truedigital.common.share.componentv3.injections

import com.truedigital.common.share.analytics.injections.AnalyticsSubComponent
import com.truedigital.common.share.componentv3.di.SearchAnimationBindsModule
import com.truedigital.common.share.componentv3.widget.CommonAppBar
import com.truedigital.common.share.currentdate.injections.CurrentDateSubComponent
import com.truedigital.common.share.data.coredata.injections.CoreDataSubComponent
import com.truedigital.common.share.datalegacy.injections.DataLegacySubComponent
import com.truedigital.core.injections.CoreSubComponent
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigSubComponent
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        SearchAnimationBindsModule::class
    ],
    dependencies = [
        AnalyticsSubComponent::class,
        CoreDataSubComponent::class,
        CoreSubComponent::class,
        DataLegacySubComponent::class,
        FirestoreConfigSubComponent::class,
        CurrentDateSubComponent::class
    ]
)
interface ComponentV3Component {

    companion object {

        private lateinit var componentV3Component: ComponentV3Component

        fun initialize(componentV3Component: ComponentV3Component) {
            this.componentV3Component = componentV3Component
        }

        fun getInstance(): ComponentV3Component {
            if (!(::componentV3Component.isInitialized)) {
                error("ComponentV3Component not initialize")
            }
            return componentV3Component
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            analyticsSubComponent: AnalyticsSubComponent,
            coreDataSubComponent: CoreDataSubComponent,
            coreSubComponent: CoreSubComponent,
            dataLegacySubComponent: DataLegacySubComponent,
            firestoreConfigSubComponent: FirestoreConfigSubComponent,
            currentDateSubComponent: CurrentDateSubComponent
        ): ComponentV3Component
    }

    fun inject(appBar: CommonAppBar)
    fun getComponentV3SubComponent(): ComponentV3SubComponent
}

@Subcomponent
interface ComponentV3SubComponent {
}
