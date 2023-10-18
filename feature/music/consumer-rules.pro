# Data Layer =======================================================================================
-keep class
    com.truedigital.features.tuned.**.model.**,
    com.truedigital.features.music.data.**.model.**,
    com.truedigital.features.tuned.data.download.ImageManager$Companion,
    com.truedigital.features.tuned.data.util.LocalisedString
    {
    <fields>;
    public <init>(...);
    <methods>;
    }

# Domain Layer =====================================================================================
-keep class
    com.truedigital.features.music.domain.**.model.**
    { <fields>; <methods>; }