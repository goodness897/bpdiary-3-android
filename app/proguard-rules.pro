# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\dmkin\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

-verbose

-dontoptimize
-dontskipnonpubliclibraryclasses

-keepattributes SourceFile,LineNumberTable

#-libraryjars libs/httpcore-4.3.1.jar
#-libraryjars ./libs/httpmime-4.3.1.jar

#Crashlytics
-libraryjars libs
-keep class com.crashlytics.** { *; }

#오픈 CSV
#-libraryjars libs/opencsv-2.3.jar
#-dontwarn au.com.bytecode.opencsv.bean.**
#-keep class au.com.bytecode.opencsv.bean.** { *; }
-dontwarn au.com.bytecode.opencsv.bean.**

#InAppBilling
-keep class kr.co.openit.bpdiary.iab.utils.** { *; }
-keep class com.android.vending.billing.** { *; }

#ActionbarSherlock
-dontwarn com.actionbarsherlock.**
-keep class com.actionbarsherlock.** { *; }
-keep interface com.actionbarsherlock.** { *; }

#SlidingMenu_Lib
#-keep class com.jeremyfeinstein.slidingmenu.lib.** { *; }
#-keep class com.jeremyfeinstein.slidingmenu.lib.app.** { *; }

#삼성
-dontwarn com.samsung.**

#안드로이드 기본
#-dontwarn android.support.v4.app.**
#-dontwarn android.app.**
#-keep class android.support.v4.app.** { *; }
#-keep interface android.support.v4.app.** { *; }

#google-play-services_lib & google fit
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class kr.co.openit.bpdiary.common.view.**{ *; }

##apache 시리즈
#-dontwarn org.apache.**
#-dontwarn org.apache.http.**
#-dontwarn org.apache.http.params.**
#-keep class org.apache.** { *; }
#-keep interface org.apache.** { *; }
#
#-keepattributes *Annotation*