# Conceal ==========================================================================================
-keep,allowobfuscation @interface com.facebook.crypto.proguard.annotations.DoNotStrip
-keep,allowobfuscation @interface com.facebook.crypto.proguard.annotations.KeepGettersAndSetters
-keep @com.facebook.crypto.proguard.annotations.DoNotStrip class *
-keepclassmembers class * { @com.facebook.crypto.proguard.annotations.DoNotStrip *; }
-keepclassmembers @com.facebook.crypto.proguard.annotations.KeepGettersAndSetters class * {
  void set*(***);
  *** get*();
}

# Jackson ==========================================================================================
-keep enum com.fasterxml.jackson.** { *; }

# AVLoadingIndicatorView ===========================================================================
-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }