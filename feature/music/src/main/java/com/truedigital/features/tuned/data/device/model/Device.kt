package com.truedigital.features.tuned.data.device.model

data class Device(
    var displayName: String,
    var uniqueId: String,
    var token: String,
    var type: String,
    var operatingSystem: String,
    var operatingSystemVersion: String,
    var appVersion: String,
    var country: String,
    var language: String,
    var manufacturer: String,
    var timezoneOffset: Int,
    var carrier: String,
    var brand: String,
    var referrer: String?
)
