# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the SDK directory.

# Keep TFLite model files
-keep class org.tensorflow.lite.** { *; }
-keep class com.braillevision.v2.data.tflite.** { *; }

# Keep data classes for Room
-keep class com.braillevision.v2.data.local.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }
