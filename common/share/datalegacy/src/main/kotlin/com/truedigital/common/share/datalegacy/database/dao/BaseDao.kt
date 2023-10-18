package com.truedigital.common.share.datalegacy.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.truedigital.common.share.datalegacy.database.entity.BaseEntity
import java.lang.reflect.ParameterizedType

const val SHELF_SLUG = "shelfSlug"
const val SHELF_CODE = "shelfCode"

abstract class BaseDao<T : BaseEntity> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAll(obj: List<T>)

    @Update
    abstract suspend fun update(obj: T)

    @Insert
    abstract suspend fun update(vararg obj: T)

    @Delete
    abstract suspend fun delete(obj: T)

    @RawQuery
    abstract suspend fun doFindAllValid(query: SupportSQLiteQuery): List<T>?

    @RawQuery
    abstract suspend fun doFind(query: SupportSQLiteQuery): T?

    @RawQuery
    abstract suspend fun doFindTS(query: SupportSQLiteQuery): Long?

    @RawQuery
    abstract suspend fun doDeleteAll(query: SupportSQLiteQuery): Int

    suspend fun selectAll(): List<T>? {
        return try {
            val query = "select * from " + getTableName()
            val simpleSQLiteQuery = SimpleSQLiteQuery(query)
            doFindAllValid(simpleSQLiteQuery)
        } catch (e: SQLiteException) {
            null
        }
    }

    suspend fun selectByKey(key: String, value: String): T? {
        return try {
            val query = "select * from " + getTableName() + " where $key = ?"
            val simpleSQLiteQuery = SimpleSQLiteQuery(query, arrayOf<Any>(value))
            doFind(simpleSQLiteQuery)
        } catch (e: SQLiteException) {
            null
        }
    }

    suspend fun selectListByKey(key: String, value: String): List<T>? {
        return try {
            val query = "select * from " + getTableName() + " where $key = ?"
            val simpleSQLiteQuery = SimpleSQLiteQuery(query, arrayOf<Any>(value))
            doFindAllValid(simpleSQLiteQuery)
        } catch (e: SQLiteException) {
            null
        }
    }

    suspend fun getTimestamp(): Long? {
        return try {
            val query = "select timeStamp from " + getTableName()
            val simpleSQLiteQuery = SimpleSQLiteQuery(query)
            doFindTS(simpleSQLiteQuery)
        } catch (e: SQLiteException) {
            null
        }
    }

    suspend fun getTimestampByKey(key: String, value: String): Long? {
        return try {
            val query = "select timeStamp from " + getTableName() + " where $key = ?"
            val simpleSQLiteQuery = SimpleSQLiteQuery(query, arrayOf<Any>(value))
            doFindTS(simpleSQLiteQuery)
        } catch (e: SQLiteException) {
            null
        }
    }

    suspend fun deleteByKey(key: String, value: String): Int {
        val query = "delete from " + getTableName() + " where $key = ?"
        val simpleSQLiteQuery = SimpleSQLiteQuery(query, arrayOf<Any>(value))
        return doDeleteAll(simpleSQLiteQuery)
    }

    suspend fun deleteAll(): Int {
        val query = "delete from " + getTableName()
        val simpleSQLiteQuery = SimpleSQLiteQuery(query)
        return doDeleteAll(simpleSQLiteQuery)
    }

    private fun getTableName(): String? {
        val clazz = (javaClass.superclass?.genericSuperclass as ParameterizedType?)
            ?.actualTypeArguments?.get(0) as Class<*>
        return clazz.simpleName
    }
}
