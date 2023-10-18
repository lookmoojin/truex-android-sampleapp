# Data =============================================================================================
-keep class
    com.truedigital.core.data.**,
    com.truedigital.core.constant.FireBaseConstant,
    com.truedigital.core.constant.SharedPrefsKeyConstant
    { *; }

# JsonExtension ====================================================================================
-keep class
    com.truedigital.core.extensions.JsonExtensionKt.**
    { *; }

# GsonUtil =========================================================================================
-keep class
    com.truedigital.core.utils.GsonUtil.** {
    <fields>;
    <methods>;
    private <fields>;
    private <methods>;
 }

# SharedPrefsUtils =================================================================================
-keep class
    com.truedigital.core.utils.SharedPrefsUtils.**,
    com.truedigital.core.utils.SharedPrefsInterface
    { <fields>; <methods>; }
