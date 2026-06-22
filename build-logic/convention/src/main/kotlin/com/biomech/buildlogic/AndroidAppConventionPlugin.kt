package com.biomech.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import com.android.build.gradle.AppExtension

class AndroidAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")

            extensions.configure<AppExtension> {
                compileSdk = 34
                defaultConfig {
                    minSdk = 26
                    targetSdk = 34
                    applicationId = "com.biomech.app"
                    versionCode = 1
                    versionName = "0.1.0"
                }
            }
        }
    }
}
