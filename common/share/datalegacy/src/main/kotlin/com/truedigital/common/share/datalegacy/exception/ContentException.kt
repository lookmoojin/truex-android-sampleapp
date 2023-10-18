package com.truedigital.common.share.datalegacy.exception

import java.lang.Exception

class ContentException : Exception() {
    companion object {
        const val CODE_FOR_CHECK_SUCCESS_FROM_API_INT = 200
        const val CODE_FOR_CHECK_SUCCESS_FROM_API = "200"
        const val MESSAGE_GET_SHELF_LIST_FAILED = "failed to load shelf list"
        const val MESSAGE_GET_CONTENT_LIST_FAILED = "failed to load content list"
        const val MESSAGE_GET_CCU_FAILED = "failed to load CCU"
        const val MESSAGE_ERROR_LOAD_EPG_CONTENT_FAILED = "load epg content failed"
        const val MESSAGE_ERROR_LOAD_RECOMMEND_CONTENT_FAILED = "load recommend content failed"
        const val MESSAGE_GET_PACKAGE_ALACARTE_LIST_EMPTY = "load content list empty"
    }
}
