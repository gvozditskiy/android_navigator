plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    kotlin("kapt")
}

android {
    compileSdkVersion(Android.TARGET_SDK)

    defaultConfig {
        minSdkVersion(Android.MIN_SDK)
        targetSdkVersion(Android.TARGET_SDK)
        versionCode = 1
        versionName = "1.0"

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(Deps.KOTLIN_STDLIB)
    implementation(Deps.Androidx.APPCOMPAT)
    implementation(Deps.Androidx.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Deps.TIMBER)
}