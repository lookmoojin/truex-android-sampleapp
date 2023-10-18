package com.truedigital.component.dialog.trueid

import com.truedigital.component.R

enum class DialogIconType(
    val icon: DialogIcon?,
    val ignoreTint: Boolean = false
) {
    ALLOW_LOCATION(
        AdaptiveIcon(R.drawable.ic_dialog_allow_location_permission),
        true
    ),
    CALL_FAIL(
        AdaptiveIcon(R.drawable.ic_dialog_call_fail)
    ),
    CONTACT(
        AdaptiveIcon(R.drawable.ic_dialog_contact)
    ),
    COMMUNITY(
        AdaptiveIcon(R.drawable.ic_dialog_community)
    ),
    CRITICAL(
        AdaptiveIcon(R.drawable.ic_dialog_critical)
    ),
    DELETE(
        AdaptiveIcon(R.drawable.ic_dialog_delete)
    ),
    DEVICE_LIMIT(
        AdaptiveIcon(R.drawable.ic_dialog_devices),
        true
    ),
    FOLDER(
        AdaptiveIcon(R.drawable.ic_dialog_folder)
    ),
    GAME(
        AdaptiveIcon(R.drawable.ic_dialog_game)
    ),
    INFO(
        AdaptiveIcon(R.drawable.ic_dialog_info)
    ),
    LOGOUT(
        AdaptiveIcon(R.drawable.ic_dialog_logout)
    ),
    NO_CONTENT(
        AdaptiveIcon(R.drawable.ic_dialog_no_content)
    ),
    NO_CONNECTION(
        AdaptiveIcon(R.drawable.ic_dialog_no_connection)
    ),
    SEARCH_ERROR(
        AdaptiveIcon(R.drawable.ic_dialog_search_error)
    ),
    SUCCESS(
        AdaptiveIcon(R.drawable.ic_dialog_success)
    ),
    USER_REMOVE(
        AdaptiveIcon(R.drawable.ic_dialog_user_remove)
    ),
    WARNING(
        AdaptiveIcon(R.drawable.ic_dialog_warning)
    ),
    IMPROVE_SYSTEM(
        ThemedIcon(
            lightRes = R.drawable.ic_dialog_improve_system,
            darkRes = R.drawable.ic_dialog_improve_system_dark
        ),
        true
    ),
    MY_SERVICES(
        AdaptiveIcon(R.drawable.ic_dialog_my_services),
        true
    ),
    HOME_LANDING(
        AdaptiveIcon(R.drawable.ic_dialog_home_landing),
        true
    ),
    OTHER(null)
}
