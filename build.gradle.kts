// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
<<<<<<< HEAD
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
=======
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
>>>>>>> 2166c9ec5344b64a51ed10bd1d8f5174947e46c1
}