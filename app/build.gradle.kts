import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("org.jlleitschuh.gradle.ktlint")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("com.google.devtools.ksp")
}

android {
    val target = 35
    compileSdk = target
    defaultConfig {
        applicationId = "com.softklass.warbler"
        minSdk = 26
        targetSdk = target
        versionCode = 76
        versionName = "0.$versionCode"
        testInstrumentationRunner = "com.warbler.config.HiltAndroidJUnitRunner"
        testInstrumentationRunnerArguments.putAll(mutableMapOf("clearPackageData" to "true"))
        vectorDrawables.useSupportLibrary = true
        buildConfigField("String", "WEATHER_BASE_URL", "\"${System.getenv("WEATHER_BASE_URL") ?: project.property("WEATHER_BASE_URL")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "WEATHER_API_KEY", "\"${System.getenv("WEATHER_API_KEY") ?: project.property("WEATHER_API_KEY")}\"")
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
            buildConfigField("String", "WEATHER_API_KEY", "\"${System.getenv("WEATHER_API_KEY") ?: project.property("WEATHER_API_KEY")}\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
        animationsDisabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
    namespace = "com.warbler"

    packaging {
        resources {
            excludes += "META-INF/*"
        }
    }
}

ktlint {
    android = true
    ignoreFailures = false
    reporters {
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.JSON)
        reporter(ReporterType.HTML)
    }
}

tasks.named("preBuild") {
    dependsOn("ktlintFormat")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    //  Android specific
    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.extensions)
    implementation(libs.play.services.location)
    implementation(libs.material)
    implementation(libs.viewpager2)

    // DI
    implementation(libs.hilt.android)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)

    // Firebase
    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))

    // Add the dependencies for the Crashlytics and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.crashlytics.ktx)

    // Add the dependency for the Realtime Database library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.database.ktx)

    // Add the dependency for the Firebase SDK for Google Analytics
    implementation(libs.firebase.analytics.ktx)

    // Add Performance lib
    implementation(libs.firebase.perf.ktx)

    // This dependency is downloaded from the Google’s Maven repository.
    // Make sure you also include that repository in your project's build.gradle file.
    implementation(libs.feature.delivery)

    // For Kotlin users, also import the Kotlin extensions library for Play Feature Delivery:
    implementation(libs.feature.delivery.ktx)

    // In App Updates
    implementation(libs.app.update)
    // For Kotlin users also add the Kotlin extensions library for Play In-App Update:
    implementation(libs.app.update.ktx)

    // Graphs and charts
    // Houses the core logic for charts and other elements. Included in all other modules.
    implementation(libs.core)

    // Vico for the view system.
    implementation(libs.views)

    // For instrumented tests.
    androidTestImplementation(libs.hilt.android.testing)
    // ...with Kotlin.
    kaptAndroidTest(libs.hilt.android.compiler)

    //  Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Kotlin
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.kotlinx.serialization.json)

    // Feature module Support
    implementation(libs.navigation.dynamic.features.fragment)

    // Testing Navigation
    androidTestImplementation(libs.navigation.testing)

    //  data
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.datastore.preferences)
    implementation(libs.datastore.preferences.core)

    // network
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.okhttp)
    implementation(libs.browser)

    //  testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.rules)
    androidTestImplementation(libs.accessibility.test.framework)
    androidTestImplementation(libs.espresso.library.kotlin)

    androidTestUtil(libs.orchestrator)
}
