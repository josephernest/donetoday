# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/leubner/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 1

-dontnote org.apache.http.**
-dontnote android.net.http.**
-dontwarn com.fasterxml.jackson.**

##############################################################################
# needed for https://github.com/Gericop/Android-Support-Preference-V7-Fix 24.0.0.1
-keep class * extends android.support.v7.preference.PreferenceGroupAdapter {
   *;
}
-keep class android.support.v7.preference.PreferenceGroupAdapter$PreferenceLayout {
   *;
}
# TTD: Preference headers specify fragment classes which must be preserved
-keep class * extends android.support.v7.preference.PreferenceFragmentCompat {
   public void *(android.view.View);
}
-keep class android.support.v7.preference.** { *; }


-keep class com.als.donetoday.db.LifeLogEntry {
   *;
}

-keepclassmembers class com.als.donetoday.db.LifeLogContract$LifeLogEntryTable {
   public static java.lang.String COLUMN_NAME_LIFELOGENTRY_*;
}

-keep class com.fasterxml.jackson.** {
   *;
}

