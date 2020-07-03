import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    id("kotlin-android")
}

android {
    compileSdkVersion(Android.TARGET_SDK)

    defaultConfig {
        applicationId = "com.runningcat.navigator"
        minSdkVersion(Android.MIN_SDK)
        targetSdkVersion(Android.TARGET_SDK)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Deps.KOTLIN_STDLIB)
    implementation(Deps.TIMBER)

    implementation(Deps.Androidx.APPCOMPAT)
    implementation(Deps.Androidx.MATERIAL)
    implementation(Deps.Androidx.CORE_KTX)
    implementation(Deps.Androidx.CONSTRAINT_LAYOUT)
    implementation(Deps.Androidx.LIFECYCLE_VIEWMODEL_KTX)
    implementation(Deps.Androidx.LIFECYCLE_EXT)

    androidTestImplementation(Deps.Androidx.Test.JUNIT_EXT)
    androidTestImplementation(Deps.Androidx.Test.ESPRESSO_CORE)
}
