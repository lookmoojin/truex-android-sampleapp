package com.truedigital.common.share.analytics.measurement.base

open class BaseAnalyticModel {
    var appName: String = ""
    var deviceId: String = ""
    var userId: String = "" // ssoid
    var location: String = ""
    var ppid: String = ""
    var wifiInfo: String = ""
    var networkInfo: List<String> = mutableListOf("", "", "")
}
