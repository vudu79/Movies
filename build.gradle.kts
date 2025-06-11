// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
}



buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
    }

    extra.apply {
        set("androidxAnnotationVersion", "1.5.0")
        set("robolectricVersion", "4.13")
        set("guavaVersion", "31.1 - android")
        set("extTruthVersion", "1.6.0")
        set("coreVersion", "1.6.1")
        set("extJUnitVersion", "1.2.1")
        set("runnerVersion", "1.6.1")
        set("espressoVersion", "3.6.1")
    }
}
