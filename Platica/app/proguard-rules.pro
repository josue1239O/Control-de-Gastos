# ============================================================================
#  Reglas de ProGuard/R8 para Platica
# ============================================================================

# ----- SQLCipher -----
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-dontwarn net.sqlcipher.**

# ----- kotlinx.serialization (respaldos JSON) -----
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.bolivia.platica.**$$serializer { *; }
-keepclassmembers class com.bolivia.platica.** {
    *** Companion;
}
-keepclasseswithmembers class com.bolivia.platica.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ----- Room -----
-keep class * extends androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.paging.**

# ----- Hilt / Dagger -----
-dontwarn dagger.hilt.**

# ----- Modelo (reflection en serialización) -----
-keep class com.bolivia.platica.data.local.entity.** { *; }

# ----- Tink / security-crypto (errorprone annotations) -----
-dontwarn com.google.errorprone.annotations.**
-dontwarn com.google.crypto.tink.**
-keep class com.google.crypto.tink.** { *; }
-keep class com.google.api.** { *; }
-dontwarn javax.annotation.**
-dontwarn org.joda.time.**

