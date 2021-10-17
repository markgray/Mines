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
#
## Specifies to write out the entire configuration that has been parsed, with included files and
# replaced variables. The structure is printed to the standard output or to the given file.
# This can sometimes be useful to debug configurations, or to convert XML configurations into
# a more readable format.
#-printconfiguration /home/markgray/tmp/configuration.txt
#
## Specifies to list dead code of the input class files. The list is printed to the standard output
# or to the given file. For example, you can list the unused code of an application. Only applicable
# when shrinking.
#-printusage /home/markgray/tmp/usage.txt
#
## The report of kept entry points Specifies to exhaustively list classes and class members matched
# by the various -keep options. The list is printed to the standard output or to the given file.
# The list can be useful to verify if the intended class members are really found, especially if
# you're using wildcards. For example, you may want to list all the applications or all the applets
# that you are keeping.
#-printseeds /home/markgray/tmp/seeds.txt
#
## Specifies to print the mapping from old names to new names for classes and class members that
# have been renamed. The mapping is printed to the standard output or to the given file.
# For example, it is required for subsequent incremental obfuscation, or if you ever want to make
# sense again of obfuscated stack traces. Only applicable when obfuscating.
#-printmapping /home/markgray/tmp/mapping.txt

# Allows you to specify supported Java attributes for R8 to retain in the code
# The LineNumberTable attribute is needed for disambiguating between optimized positions inside
# methods. The SourceFile attribute is necessary for getting line numbers printed in a stack trace
# on a virtual machine or device. -renamesourcefileattribute will set the source file in stack
# traces to just SourceFile. The actual original source file name is not required when retracing as
# the mapping file contains the original source file.
-keepattributes LineNumberTable,SourceFile
# Specifies a constant string to be put in the SourceFile attributes (and SourceDir attributes) of
# the class files. Note that the attribute has to be present to start with, so it also has to be
# preserved explicitly using the -keepattributes directive. For example, you may want to have your
# processed libraries and applications produce useful obfuscated stack traces. Only applicable when
# obfuscating.
-renamesourcefileattribute SourceFile
