# ORBIT ProGuard / R8 rules

# Room
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }

# Hilt / Dagger
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.orbit.spatialjournal.**$$serializer { *; }
-keepclassmembers class com.orbit.spatialjournal.** { *** Companion; }
-keepclasseswithmembers class com.orbit.spatialjournal.** { kotlinx.serialization.KSerializer serializer(...); }

# Google Maps / Play Services
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }

# Keep Memory model + entities fully (used for JSON export/import and reflection-based mapping)
-keep class com.orbit.spatialjournal.core.model.** { *; }
-keep class com.orbit.spatialjournal.data.local.entity.** { *; }
