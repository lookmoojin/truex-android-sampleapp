package com.truedigital.share.data.firestoreconfig.initializer

import android.content.Context
import androidx.startup.Initializer
import com.truedigital.core.CoreInitializer
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.injections.CoreComponent
import com.truedigital.share.data.firestoreconfig.domainconfig.usecase.GetDomainServiceApiDataUseCase
import com.truedigital.share.data.firestoreconfig.initialappconfig.usecase.GetInitialAppConfigUsecase
import com.truedigital.share.data.firestoreconfig.injections.DaggerFirestoreConfigComponent
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.time.ExperimentalTime

class FirestoreConfigInitializer : Initializer<FirestoreConfigComponent> {

    @OptIn(ExperimentalTime::class)
    override fun create(context: Context): FirestoreConfigComponent {
        return DaggerFirestoreConfigComponent.factory().create(
            CoreComponent.getInstance().getCoreSubComponent()
        ).apply {
            FirestoreConfigComponent.initialize(this)
        }.also {
            it.getFirestoreConfigSubComponent().getGetFireBaseSecureUseCase().execute(context)

            CoroutineScope(Dispatchers.IO).launchSafe {
                val getDomainServiceApiDataUseCase: GetDomainServiceApiDataUseCase =
                    it.getFirestoreConfigSubComponent()
                        .getGetDomainServiceApiDataUseCase()

                val getInitialAppConfigUseCase: GetInitialAppConfigUsecase =
                    it.getFirestoreConfigSubComponent()
                        .getGetInitialAppConfigUsecase()

                getDomainServiceApiDataUseCase.execute()
                getInitialAppConfigUseCase.execute()
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreInitializer::class.java
    )
}
