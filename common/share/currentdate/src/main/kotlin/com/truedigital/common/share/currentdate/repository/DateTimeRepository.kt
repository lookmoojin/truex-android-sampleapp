package com.truedigital.common.share.currentdate.repository

import com.truedigital.common.share.currentdate.repository.dmp.DmpDateTimeInterface
import com.truedigital.common.share.currentdate.repository.nondmp.NonDmpCurrentDateTimeInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.apache.commons.net.ntp.NTPUDPClient
import java.net.InetAddress
import java.util.Calendar
import java.util.Date

interface DateTimeRepository {
    fun getFirebaseDateTime(): Flow<Long>
    fun getGoogleDateTime(): Flow<Long>
    fun getLocalDate(): Date
    fun getLocalDateTime(): Long
    fun getServerDateTime(): Flow<Long>
}

class DateTimeRepositoryImpl(
    private val timeClient: NTPUDPClient,
    private val dmpAPIInterface: DmpDateTimeInterface,
    private val nonDmpAPIInterface: NonDmpCurrentDateTimeInterface
) : DateTimeRepository {

    private companion object {
        const val DEFAULT_TIME_STAMP = 0L
        const val GOOGLE_HOST = "time.google.com"
        const val MESSAGE_SERVER_ERROR = "response of getServerDateTime is failure"
        const val MESSAGE_FIREBASE_ERROR = "response of getFirebaseDateTime is failure"
    }

    override fun getFirebaseDateTime(): Flow<Long> {
        return flow {
            val response = nonDmpAPIInterface.getCurrentDateTime()
            if (response.isSuccessful) {
                response.body()?.let { _serverDateTime ->
                    emit(_serverDateTime.date)
                } ?: emit(DEFAULT_TIME_STAMP)
            } else {
                throw IllegalStateException(MESSAGE_FIREBASE_ERROR)
            }
        }
    }

    override fun getGoogleDateTime(): Flow<Long> {
        return flow {
            val iNet = InetAddress.getByName(GOOGLE_HOST)
            val timeInfo = timeClient.getTime(iNet)
            val timestamp =
                timeInfo?.message?.transmitTimeStamp?.time ?: DEFAULT_TIME_STAMP
            emit(timestamp)
        }.flowOn(Dispatchers.IO)
    }

    override fun getLocalDate(): Date {
        return Calendar.getInstance().time
    }

    override fun getLocalDateTime(): Long {
        return Calendar.getInstance().timeInMillis
    }

    override fun getServerDateTime(): Flow<Long> {
        return flow {
            val response = dmpAPIInterface.getServerDateTime()
            when {
                (response.isSuccessful) -> {
                    response.body()?.let { _serverDateTime ->
                        emit(_serverDateTime.date)
                    } ?: emit(DEFAULT_TIME_STAMP)
                }
                else -> {
                    throw IllegalStateException(MESSAGE_SERVER_ERROR)
                }
            }
        }
    }
}
