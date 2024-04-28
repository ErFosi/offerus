// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false

    //
    id ("com.google.dagger.hilt.android") version "2.48.1" apply false

    // version 1.9.0 en lugar de 1.9.22
    kotlin("jvm") version "1.9.0" // or kotlin("multiplatform") or any other kotlin plugin
    kotlin("plugin.serialization") version "1.9.0"


    id("com.google.gms.google-services") version "4.4.1" apply false
}