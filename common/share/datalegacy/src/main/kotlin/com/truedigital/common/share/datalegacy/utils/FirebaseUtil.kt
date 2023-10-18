package com.truedigital.common.share.datalegacy.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.truedigital.core.BuildConfig
import com.truedigital.core.constant.FireBaseConstant
import com.truedigital.core.extensions.environmentCase
import javax.inject.Inject

const val COLLECTION_CONCURRENT = "concurrency"

interface FirebaseUtilInterface {
    fun registerApps(context: Context, ssoId: String)
    fun resetDatabasesUrl(ssoId: String)
    fun getDatabase(useDB: String): FirebaseDatabase
    fun getDatabaseUrl(useDB: String): String
    fun getDatabaseForDeviceLimitation(): FirebaseDatabase
    fun getDatabaseForOttPayment(): FirebaseDatabase
    fun getDatabaseDefault(): FirebaseDatabase
    fun getRealTime(): FirebaseApp
    fun getUsageMeter(): FirebaseApp
    fun getConfigTopbar(): FirebaseApp
    fun enableDebugingLog()
}

class FirebaseUtil @Inject constructor() : FirebaseUtilInterface {

    private val environment = BuildConfig.ENVIRONMENT
    private val isDev = BuildConfig.ENVIRONMENT_IS_DEV

    private val realtimeStagingDBKeys = arrayOf(
        "trueid-staging-6b2db",
        "trueid-staging-6e73d",
        "trueid-staging-713f7",
        "trueid-staging-a091d",
        "trueid-staging-b26dd",
        "trueid-staging-c7e8d",
        "trueid-staging-d9495"
    )

    private val realtimePreProdDBKeys = arrayOf(
        "trueid-preprod-20834",
        "trueid-preprod-238f3",
        "trueid-preprod-2e543",
        "trueid-preprod-63b4c",
        "trueid-preprod-d6817",
        "trueid-preprod-df01b",
        "trueid-preprod-f7cdd"
    )

    private val realtimeDBKeys = arrayOf(
        "trueid-84d04-292d7",
        "trueid-84d04-aef07",
        "trueid-84d04-c1a9e",
        "trueid-84d04-c7f68",
        "trueid-84d04-d2c9b",
        "trueid-84d04-e1b05",
        "trueid-84d04-edc61"
    )

    private val usageMeterDBKeys = arrayOf(
        "usage-meter-a1111-2b527",
        "usage-meter-a1111-3488f",
        "usage-meter-a1111-36668",
        "usage-meter-a1111-50658",
        "usage-meter-a1111-76320",
        "usage-meter-a1111-b7254",
        "usage-meter-a1111-f28e8"
    )

    private val usageMeterPreProdDBKeys = arrayOf(
        "usage-meter-preprod-1f744",
        "usage-meter-preprod-59843",
        "usage-meter-preprod-73505",
        "usage-meter-preprod-83f1b",
        "usage-meter-preprod-8f98f",
        "usage-meter-preprod-9055a",
        "usage-meter-preprod-fe834"
    )

    private val usageMeterStagingDBKeys = arrayOf(
        "usage-meter-staging-5fd06",
        "usage-meter-staging-683c2",
        "usage-meter-staging-7a23f",
        "usage-meter-staging-8900c",
        "usage-meter-staging-8a710",
        "usage-meter-staging-a1b10",
        "usage-meter-staging-f08de"
    )

    private var ssoId: String = ""

    private lateinit var context: Context

    private lateinit var realTimeEndpoint: String
    private lateinit var usageMeterEndpoint: String
    private lateinit var usageMeterTopbarEndpoint: String

    companion object {
        // Constant
        const val FIREBASE_URL_PATTERN = "https://%s.firebaseio.com"
        const val MASTER_REALTIME_STAGING_KEY = "trueid-staging"
        const val MASTER_REALTIME_PREPROD_KEY = "trueid-preprod"
        const val MASTER_REALTIME_KEY = "trueid-84d04"
        const val MASTER_USAGEMETER_STAGING_KEY = "usage-meter-staging-no-login"
        const val MASTER_USAGEMETER_PREPROD_KEY = "usage-meter-preprod-no-login"
        const val MASTER_USAGEMETER_KEY = "usage-meter-a1111-no-login"
        const val USAGEMETER_REALTIME_APPNAME = FireBaseConstant.USAGEMETER_REAL_TIME_DB_APP_NAME
        const val REALTIME_APPNAME = FireBaseConstant.REAL_TIME_DB_APP_NAME

        const val USAGE_METER_TOPBAR_APPNAME = FireBaseConstant.USAGEMETER_TOPBAR_APP_NAME
        const val USAGE_METER_TOPBAR_KEY = "usage-meter-sdk-staging"

        // For device limitation feature
        const val MASTER_USAGEMETER_STAGING_KEY_FOR_DEVICE_LIMITATION = "usage-meter-staging-ab384"
        const val MASTER_USAGEMETER_PREPROD_KEY_FOR_DEVICE_LIMITATION = "usage-meter-preprod-device-limit"
        const val MASTER_USAGEMETER_KEY_FOR_DEVICE_LIMITATION = "usage-meter-a1111-d64d7"

        // For ott payment via qr scan feature
        const val MASTER_USAGEMETER_STAGING_KEY_FOR_OTT_PAYMENT = "usage-meter-staging"
        const val MASTER_USAGEMETER_PREPROD_KEY_FOR_OTT_PAYMENT = "usage-meter-preprod"
        const val MASTER_USAGEMETER_KEY_FOR_OTT_PAYMENT = "usage-meter-a1111"

        // Singleton
        @JvmStatic
        val instance: FirebaseUtil by lazy {
            FirebaseUtil()
        }
    }

    // Reset firebase database url
    override fun resetDatabasesUrl(ssoId: String) {
        this.ssoId = ssoId
        setDatabaseUrl(REALTIME_APPNAME)
        setDatabaseUrl(USAGEMETER_REALTIME_APPNAME)
        setDatabaseUrl(USAGE_METER_TOPBAR_APPNAME)
    }

    override fun registerApps(context: Context, ssoId: String) {
        this.context = context
        this.ssoId = ssoId

        setDatabaseUrl(USAGEMETER_REALTIME_APPNAME)
        registerApp(USAGEMETER_REALTIME_APPNAME)

        setDatabaseUrl(REALTIME_APPNAME)
        registerApp(REALTIME_APPNAME)

        setDatabaseUrl(USAGE_METER_TOPBAR_APPNAME)
        registerApp(USAGE_METER_TOPBAR_APPNAME)
    }

    override fun getDatabase(useDB: String): FirebaseDatabase {
        return when (useDB) {
            USAGEMETER_REALTIME_APPNAME -> FirebaseDatabase.getInstance(usageMeterEndpoint)
            REALTIME_APPNAME -> FirebaseDatabase.getInstance(realTimeEndpoint)
            USAGE_METER_TOPBAR_APPNAME -> FirebaseDatabase.getInstance(usageMeterTopbarEndpoint)
            else -> FirebaseDatabase.getInstance(realTimeEndpoint)
        }
    }

    override fun getDatabaseUrl(useDB: String): String {
        return when (useDB) {
            USAGEMETER_REALTIME_APPNAME -> usageMeterEndpoint
            REALTIME_APPNAME -> realTimeEndpoint
            USAGE_METER_TOPBAR_APPNAME -> usageMeterTopbarEndpoint
            else -> realTimeEndpoint
        }
    }

    override fun getDatabaseForDeviceLimitation(): FirebaseDatabase {
        val dbPath: String = environment.environmentCase(
            staging = {
                MASTER_USAGEMETER_STAGING_KEY_FOR_DEVICE_LIMITATION
            },
            preProd = {
                MASTER_USAGEMETER_PREPROD_KEY_FOR_DEVICE_LIMITATION
            },
            prod = {
                MASTER_USAGEMETER_KEY_FOR_DEVICE_LIMITATION
            }
        )
        val databaseUrl = String.format(FIREBASE_URL_PATTERN, dbPath)
        return FirebaseDatabase.getInstance(databaseUrl)
    }

    override fun getDatabaseForOttPayment(): FirebaseDatabase {
        val dbPath = environment.environmentCase(
            staging = {
                MASTER_USAGEMETER_STAGING_KEY_FOR_OTT_PAYMENT
            },
            preProd = {
                MASTER_USAGEMETER_PREPROD_KEY_FOR_OTT_PAYMENT
            },
            prod = {
                MASTER_USAGEMETER_KEY_FOR_OTT_PAYMENT
            }
        )
        val databaseUrl = String.format(FIREBASE_URL_PATTERN, dbPath)
        return FirebaseDatabase.getInstance(databaseUrl)
    }

    override fun getDatabaseDefault(): FirebaseDatabase {
        val dbPath: String = environment.environmentCase(
            staging = {
                MASTER_USAGEMETER_STAGING_KEY
            },
            preProd = {
                MASTER_USAGEMETER_PREPROD_KEY
            },
            prod = {
                MASTER_USAGEMETER_KEY
            }
        )
        val usageMeterDefault = String.format(FIREBASE_URL_PATTERN, dbPath)
        return FirebaseDatabase.getInstance(usageMeterDefault)
    }

    override fun getRealTime(): FirebaseApp {
        return FirebaseApp.getInstance(REALTIME_APPNAME)
    }

    override fun getUsageMeter(): FirebaseApp {
        return FirebaseApp.getInstance(USAGEMETER_REALTIME_APPNAME)
    }

    override fun getConfigTopbar(): FirebaseApp {
        return FirebaseApp.getInstance(USAGE_METER_TOPBAR_APPNAME)
    }

    private fun registerApp(useDB: String) {
        val options: FirebaseOptions

        when (useDB) {
            USAGE_METER_TOPBAR_APPNAME -> {
                options = FirebaseOptions.Builder()
                    .setApplicationId(BuildConfig.FIREBASE_USAGEMETER_TOPBAR_APP_ID)
                    .setApiKey(BuildConfig.FIREBASE_USAGEMETER_TOPBAR_API_KEY)
                    .setDatabaseUrl(usageMeterTopbarEndpoint)
                    .setProjectId(BuildConfig.FIREBASE_USAGEMETER_TOPBAR_PROJECT_ID)
                    .build()
                FirebaseApp.initializeApp(context, options, useDB)
            }
            USAGEMETER_REALTIME_APPNAME -> {
                options = FirebaseOptions.Builder()
                    .setApplicationId(BuildConfig.FIREBASE_USAGEMETER_DB_APP_ID)
                    .setApiKey(BuildConfig.FIREBASE_USAGEMETER_DB_API_KEY)
                    .setDatabaseUrl(usageMeterEndpoint)
                    .setProjectId(BuildConfig.FIREBASE_USAGEMETER_PROJECT_ID)
                    .build()
                FirebaseApp.initializeApp(context, options, useDB)
            }

            REALTIME_APPNAME -> {
                options = FirebaseOptions.Builder()
                    .setApplicationId(BuildConfig.FIREBASE_DB_APP_ID)
                    .setApiKey(BuildConfig.FIREBASE_DB_API_KEY)
                    .setDatabaseUrl(realTimeEndpoint)
                    .setGcmSenderId(BuildConfig.FIREBASE_NOTIFICATION_SENDER_ID)
                    .setProjectId(BuildConfig.FIREBASE_TRUE_ID_PROJECT_ID)
                    .build()
                FirebaseApp.initializeApp(context, options, useDB)
            }
            else -> {
                options = FirebaseOptions.Builder()
                    .setApplicationId(BuildConfig.FIREBASE_DB_APP_ID)
                    .setApiKey(BuildConfig.FIREBASE_DB_API_KEY)
                    .setDatabaseUrl(realTimeEndpoint)
                    .setGcmSenderId(BuildConfig.FIREBASE_NOTIFICATION_SENDER_ID)
                    .setProjectId(BuildConfig.FIREBASE_TRUE_ID_PROJECT_ID)
                    .build()
                FirebaseApp.initializeApp(context, options, useDB)
            }
        }
    }

    private fun setDatabaseUrl(useDB: String) {
        val dbPath: String
        when (useDB) {
            USAGE_METER_TOPBAR_APPNAME -> {
                dbPath = environment.environmentCase(
                    staging = {
                        USAGE_METER_TOPBAR_KEY
                    },
                    preProd = {
                        MASTER_USAGEMETER_KEY
                    },
                    prod = {
                        MASTER_USAGEMETER_KEY
                    }
                )
                usageMeterTopbarEndpoint = String.format(FIREBASE_URL_PATTERN, dbPath)
            }
            USAGEMETER_REALTIME_APPNAME -> {
                // No login? use master
                if (ssoId.isEmpty()) {
                    dbPath = environment.environmentCase(
                        staging = {
                            MASTER_USAGEMETER_STAGING_KEY
                        },
                        preProd = {
                            MASTER_USAGEMETER_PREPROD_KEY
                        },
                        prod = {
                            MASTER_USAGEMETER_KEY
                        }
                    )
                    usageMeterEndpoint = String.format(FIREBASE_URL_PATTERN, dbPath)
                } else {
                    // Random from db pool
                    val index = getDBPoolIndex(useDB)
                    dbPath = environment.environmentCase(
                        staging = {
                            usageMeterStagingDBKeys[index]
                        },
                        preProd = {
                            usageMeterPreProdDBKeys[index]
                        },
                        prod = {
                            usageMeterDBKeys[index]
                        }
                    )
                    usageMeterEndpoint = String.format(FIREBASE_URL_PATTERN, dbPath)
                }

                if (isDev) {
                    Toast.makeText(context, "USAGEMETER_REALTIME_APPNAME= $usageMeterEndpoint", Toast.LENGTH_LONG).show()
                }
            }

            REALTIME_APPNAME -> {
                // No login? use master
                if (ssoId.isEmpty()) {
                    dbPath = environment.environmentCase(
                        staging = {
                            MASTER_REALTIME_STAGING_KEY
                        },
                        preProd = {
                            MASTER_REALTIME_PREPROD_KEY
                        },
                        prod = {
                            MASTER_REALTIME_KEY
                        }
                    )
                    realTimeEndpoint = String.format(FIREBASE_URL_PATTERN, dbPath)
                } else {
                    // Random from db pool
                    val index = getDBPoolIndex(useDB)
                    dbPath = environment.environmentCase(
                        staging = {
                            realtimeStagingDBKeys[index]
                        },
                        preProd = {
                            realtimePreProdDBKeys[index]
                        },
                        prod = {
                            realtimeDBKeys[index]
                        }
                    )
                    realTimeEndpoint = String.format(FIREBASE_URL_PATTERN, dbPath)
                }

                if (isDev) {
                    Toast.makeText(context, "REALTIME_APPNAME= $realTimeEndpoint", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                // No login? use master
                if (ssoId.isEmpty()) {
                    dbPath = environment.environmentCase(
                        staging = {
                            MASTER_REALTIME_STAGING_KEY
                        },
                        preProd = {
                            MASTER_REALTIME_PREPROD_KEY
                        },
                        prod = {
                            MASTER_REALTIME_KEY
                        }
                    )
                    realTimeEndpoint = String.format(FIREBASE_URL_PATTERN, dbPath)
                } else {
                    // Random from db pool
                    val index = getDBPoolIndex(useDB)
                    dbPath = environment.environmentCase(
                        staging = {
                            realtimeStagingDBKeys[index]
                        },
                        preProd = {
                            realtimePreProdDBKeys[index]
                        },
                        prod = {
                            realtimeDBKeys[index]
                        }
                    )
                    realTimeEndpoint = String.format(FIREBASE_URL_PATTERN, dbPath)
                }

                if (isDev) {
                    Toast.makeText(context, "REALTIME_APPNAME= $realTimeEndpoint", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getDBPoolIndex(useDB: String): Int {
        val randomNum = try {
            ssoId.toInt()
        } catch (e: NumberFormatException) {
            0
        }

        val total: Int = when (useDB) {
            USAGEMETER_REALTIME_APPNAME -> {
                environment.environmentCase(
                    staging = {
                        usageMeterStagingDBKeys.size
                    },
                    preProd = {
                        usageMeterDBKeys.size
                    },
                    prod = {
                        usageMeterDBKeys.size
                    }
                )
            }

            REALTIME_APPNAME -> {
                environment.environmentCase(
                    staging = {
                        realtimeStagingDBKeys.size
                    },
                    preProd = {
                        realtimePreProdDBKeys.size
                    },
                    prod = {
                        realtimeDBKeys.size
                    }
                )
            }

            else -> {
                environment.environmentCase(
                    staging = {
                        realtimeStagingDBKeys.size
                    },
                    preProd = {
                        realtimePreProdDBKeys.size
                    },
                    prod = {
                        realtimeDBKeys.size
                    }
                )
            }
        }

        return randomNum % total
    }

    override fun enableDebugingLog() {
        if (BuildConfig.DEBUG) {
            FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)
        }
    }
}
