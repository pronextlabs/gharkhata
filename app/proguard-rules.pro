# GharKhata ProGuard / R8 Rules

# Room SQLite
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# Kotlin Coroutines & Flow
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# AndroidX DataStore & Preferences
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# AndroidX Biometrics
-keep class androidx.biometric.** { *; }

# Keep Domain Models
-keep class com.gharkhata.app.domain.model.** { *; }
-keep class com.gharkhata.app.core.database.** { *; }
