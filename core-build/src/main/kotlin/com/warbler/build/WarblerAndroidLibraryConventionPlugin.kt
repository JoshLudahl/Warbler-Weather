package com.warbler.build

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

class WarblerAndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("com.android.library")
        pluginManager.apply("org.jetbrains.kotlin.android")
        pluginManager.apply("org.jlleitschuh.gradle.ktlint")

        extensions.configure<LibraryExtension> {
            val targetSdk = 36
            compileSdk = targetSdk

            defaultConfig {
                minSdk = 26
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("consumer-rules.pro")
            }

            buildTypes {
                maybeCreate("release").apply {
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro",
                    )
                }
            }

            compileOptions {
                sourceCompatibility = org.gradle.api.JavaVersion.VERSION_21
                targetCompatibility = org.gradle.api.JavaVersion.VERSION_21
            }

            buildFeatures {
                dataBinding = true
                viewBinding = true
                compose = true
                buildConfig = false
            }
        }

        // Kotlin toolchain
        extensions.configure<KotlinAndroidProjectExtension> {
            jvmToolchain(21)
        }

        // ktlint configuration
        extensions.configure<KtlintExtension> {
            android.set(true)
            ignoreFailures.set(false)
            reporters {
                reporter(ReporterType.CHECKSTYLE)
                reporter(ReporterType.JSON)
                reporter(ReporterType.HTML)
            }
            additionalEditorconfig.set(
                mapOf(
                    "max_line_length" to "off",
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                ),
            )
        }

        // Ensure formatting runs before build starts
        tasks.named("preBuild").configure {
            dependsOn("ktlintFormat")
        }
    }
}
