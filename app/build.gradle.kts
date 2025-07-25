plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
    id ("kotlin-kapt") // Add this line

}

kapt {
    generateStubs = true
}

android {
    namespace = "ru.vodolatskii.movies"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.vodolatskii.movies"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        buildConfig = true
        viewBinding = true

    }
}

secrets {
    // Optionally specify a different file name containing your secrets.
    // The plugin defaults to "local.properties"
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}

dependencies {
    implementation(libs.material)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)



    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.retrofit2.converter.moshi)
    implementation(libs.moshi.kotlin)

    implementation(libs.glide)

    implementation(libs.androidx.recyclerview)
    annotationProcessor(libs.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.converter.scalars)
    implementation (libs.okhttp.v492)
    implementation (libs.logging.interceptor.v492)
    implementation(libs.converter.gson.v260)
    implementation(libs.gson)

    implementation(libs.coordinatorlayout)
    implementation(libs.material)
    implementation(libs.timberkt)

    implementation(libs.timber)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.symbol.processing.api)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)


    // Testing-only dependencies
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.core.ktx)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.androidx.fragment.testing.manifest)
    androidTestImplementation(libs.androidx.fragment.testing)

    // Dagger core
    implementation (libs.dagger)
    annotationProcessor (libs.dagger.compiler)

    // If using Kotlin, add kapt (Kotlin Annotation Processing Tool)
    kapt (libs.dagger.compiler)
    kapt (libs.kotlinx.metadata.jvm)


    // For Android-specific components like Activities, Fragments, etc.
     implementation (libs.google.dagger.android)
     implementation (libs.google.dagger.android.support)
     annotationProcessor (libs.google.dagger.android.processor)

    implementation (libs.speed.dial)


}

