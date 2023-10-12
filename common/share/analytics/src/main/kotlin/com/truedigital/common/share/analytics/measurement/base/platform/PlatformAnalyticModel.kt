package com.truedigital.common.share.analytics.measurement.base.platform

import com.truedigital.common.share.analytics.measurement.base.BaseAnalyticModel

class PlatformAnalyticModel : BaseAnalyticModel() {
    var screenClass = ""
    var screenName = ""
    var wifiName = ""
    var carrier = ""
    var appsFlyerId: String = ""
    var advertisingId: String = ""
    var tab: String = ""
}
