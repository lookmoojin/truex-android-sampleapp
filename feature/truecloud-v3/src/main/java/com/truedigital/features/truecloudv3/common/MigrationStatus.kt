package com.truedigital.features.truecloudv3.common

enum class MigrationStatus(val key: String) {
    INIT("INIT"),
    PENDING("PENDING"),
    MIGRATING("MIGRATING"),
    MIGRATED("MIGRATED"),
    FAILED("FAILED")
}
