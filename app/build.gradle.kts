plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id ("kotlin-kapt")
    id ("com.google.dagger.hilt.android")
    id ("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.offerus"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.offerus"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Hilt
    implementation ("com.google.dagger:hilt-android:2.48.1")
    kapt ("com.google.dagger:hilt-compiler:2.48.1")

    //Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:${room_version}")
    //ksp("androidx.room:room-compiler:${room_version}")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:${room_version}")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0-RC.2")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.compose.material3:material3-window-size-class:1.2.0")


    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    //NAVIGATION
    val navVersion = "2.7.7"
    implementation("androidx.navigation:navigation-compose:$navVersion")

    //WINDOW SIZE
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.compose.material3:material3-window-size-class-android:1.2.1")

    // coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    // splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // rating bar
    implementation("com.github.a914-gowtham:compose-ratingbar:1.3.4")

    // maps
    implementation ("com.google.maps.android:maps-compose:2.11.4")
    implementation ("com.google.android.gms:play-services-maps:18.1.0")

    // geolocalizaccion
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    // firebase
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
    implementation("com.google.firebase:firebase-messaging-ktx")

}