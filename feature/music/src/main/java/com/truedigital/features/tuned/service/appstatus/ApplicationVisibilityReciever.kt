package com.truedigital.features.tuned.service.appstatus

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ApplicationVisibilityReciever : BroadcastReceiver() {

    companion object {
        const val APPLICATION_VISIBILITY_CHANGE_ACTION =
            "android.intent.action.APPLICATION_VISIBILITY_CHANGE"
        const val MOVE_TO_FOREGROUND_KEY = "move_to_foreground"
    }

    override fun onReceive(context: Context, intent: Intent) {
//        if (!intent.hasExtra(MOVE_TO_FOREGROUND_KEY)) return
//        val isMoveToForeground = intent.getBooleanExtra(MOVE_TO_FOREGROUND_KEY, false)
//        if (isMoveToForeground) {
//            val applicationComponent = TunedInitializer.applicationComponent
//            val authTokenRepo = applicationComponent.getAuthTokenRepo()
//            val deviceRepo = applicationComponent.getDeviceRepo()
//            val userRepo = applicationComponent.getUserRepo()
//            userRepo.get().observeOn(AndroidSchedulers.mainThread())
//                    .flatMap {
//                        if (!it.hasActiveSub) {
//                            authTokenRepo.get(deviceRepo.getUniqueId(), true)
//                                    .flatMap { userRepo.get(true) }
//                                    .flatMap { userRepo.refreshSettings() }
//                        }
//                        else Single.just(Any())
//                    }.emptySubscribe()
//        }
    }
}
