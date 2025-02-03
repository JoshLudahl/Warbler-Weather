
buildscript {
    dependencies {
        classpath(libs.gradle)
        classpath(libs.hilt.android.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.navigation.safe.args.gradle.plugin)
        classpath(libs.google.services)
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.perf.plugin)
    }
}

plugins {
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory.get())
}
