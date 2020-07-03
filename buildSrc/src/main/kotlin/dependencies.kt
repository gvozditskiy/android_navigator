object Versions {
    const val KOTLIN = "1.3.72"
    const val ANDROID_GRADLE_PLUGIN = "3.6.3"
    const val TIMBER = "4.7.1"

    object Android {
        const val MATERIAL = "1.2.0-beta01"
        const val CONSTRAINT = "2.0.0-beta4"
        const val LIFECYCLE = "2.2.0"
        const val APPCOMPAT = "1.1.0"
        const val CORE_KTX = "1.3.0"
    }
}

object Android {
    const val MIN_SDK = 21
    const val TARGET_SDK = 29
}

object Deps {

    const val KOTLIN_STDLIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.KOTLIN}"
    const val TIMBER = "com.jakewharton.timber:timber:${Versions.TIMBER}"
    const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KOTLIN}"
    const val COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.KOTLIN}"

    object Androidx {

        const val APPCOMPAT = "androidx.appcompat:appcompat:${Versions.Android.APPCOMPAT}"
        const val MATERIAL = "com.google.android.material:material:${Versions.Android.MATERIAL}"
        const val CORE_KTX = "androidx.core:core-ktx:${Versions.Android.CORE_KTX}"
        const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.Android.CONSTRAINT}"
        const val RECYCLER = "androidx.recyclerview:recyclerview:1.1.0"
        const val LIFECYCLE_EXT = "androidx.lifecycle:lifecycle-extensions:${Versions.Android.LIFECYCLE}"
        const val LIFECYCLE_VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.Android.LIFECYCLE}"

        object Test {
            const val JUNIT_EXT= "androidx.test.ext:junit:1.1.1"
            const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:3.2.0"
        }
    }

    object Test {
        const val JUNIT = "org.junit.jupiter:junit-jupiter:5.6.2"
        const val MOCKITO_INLINE = "org.mockito:mockito-inline:3.3.3"
        const val MOCKITO = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
        const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.4"
    }
}