plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
    id ("kotlin-kapt") // Add this line
}

kapt {
    generateStubs = true
}

android {
    namespace = "ru.vodolatskii.remote_module"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation (libs.adapter.rxjava3)
    implementation(libs.converter.scalars)
    implementation (libs.okhttp.v492)
    implementation (libs.logging.interceptor.v492)
    implementation(libs.converter.gson.v260)
    implementation(libs.gson)
    implementation(libs.retrofit2.converter.moshi)
    implementation(libs.moshi.kotlin)

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
}