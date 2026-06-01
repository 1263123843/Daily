# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keepclasseswithmembernames class * {
    @dagger.hilt.android.* <fields>;
}
-keepclasseswithmembernames class * {
    @dagger.hilt.android.* <methods>;
}

# Keep Room entities and DAOs
-keep @androidx.room.Entity class *
-keep class * implements androidx.room.Dao { *; }

# Keep Gson serialization
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class com.daily.app.data.remote.api.model.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep DataStore
-keepclassmembers class * {
    <init>(...);
}

# Keep general application classes
-keep public class com.daily.app.DailyApplication { *; }
-keep public class com.daily.app.receiver.UnlockReceiver { *; }
-keep public class com.daily.app.service.WallpaperService { *; }
-keep public class com.daily.app.worker.* { *; }
