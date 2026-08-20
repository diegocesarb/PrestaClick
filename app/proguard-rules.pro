# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\ECkos\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# Room security rules
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class * extends androidx.room.RoomDatabase
-keep class com.example.myapplication.data.** { *; }

# General Compose rules (usually handled by the plugin, but safe to have)
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
