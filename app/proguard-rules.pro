# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

### These files can all be found in Mines/app/build/outputs/mapping/release/ after building
## To output a full report of all the rules that R8 applies when building your project,
#-printconfiguration /home/markgray/tmp/full-r8-config.txt
## Generate a report of removed (or kept) code
#-printusage /home/markgray/tmp/usage.txt
## The report of kept entry points
#-printseeds /home/markgray/tmp/seeds.txt
#
#-keepattributes LineNumberTable,SourceFile
#-renamesourcefileattribute SourceFile
