#Room ==============================================================================================
-keep class * extends androidx.room.RoomDatabase
-keep,allowobfuscation @interface androidx.room.Entity
-keepnames @androidx.room.Entity class *

#Chucker ==============================================================================================
-keep class com.chuckerteam.** { *; }