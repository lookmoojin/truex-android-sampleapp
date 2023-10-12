package com.truedigital.common.share.datalegacy.constant

class ShareConstant {
    object ScrectKey {
        const val CLIENT_ID = "212"
        const val CLIENT_SECRET = "607ed94fb933d775bdc1abeabfd9bd35"
    }

    object SslErrorMessage {
        const val SSL_CERTIFICATE_ERROR = "SSL Certificate error."
        const val AUTHORITY_NOT_TRUSTED = "The certificate authority is not trusted."
        const val CERTIFICATE_HAS_EXPIRED = "The certificate has expired."
        const val CERTIFICATE_HOSTNAME_MISMATCH = "The certificate Hostname mismatch."
        const val CERTIFICATE_IS_NOT_YET_VALID = "The certificate is not yet valid."
        const val POST_FIX_MESSAGE = " Do you want to continue anyway?"
        const val TITLE_SSL_ERROR = "SSL Certificate Error"
        const val TITLE_BUTTON_CONTINUE = "continue"
        const val TITLE_BUTTON_CANCEL = "cancel"
    }

    object TruePoint {
        const val TRUE_POINT_TIME_STAMP = "true.point.time.stamp"
    }

    object SingleProfile {
        const val TIMEOUT = 5L
    }

    object Tag {
        const val TAG_MAIN_CONTAINER_FRAGMENT = "MainContainerFragment"
    }
}
