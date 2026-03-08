plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("warblerAndroidLibrary") {
            id = "warbler.android.library"
            implementationClass = "com.warbler.build.WarblerAndroidLibraryConventionPlugin"
            displayName = "Warbler Android Library Convention"
            description = "Common Android/Kotlin/ktlint configuration for library modules"
        }
    }
}

dependencies {
    implementation(libs.gradle)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.ktlint.gradle)
}
