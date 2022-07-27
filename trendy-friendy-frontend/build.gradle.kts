import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
}

group = "ru.spbstu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(libs.bundles.ktor.client)
                implementation(libs.tgbotapi.webapps)
                implementation(compose.runtime)
                implementation(compose.web.core)
            }
        }
        val jsTest by getting
    }
}

tasks.named<KotlinWebpack>("jsBrowserProductionWebpack") {
    outputFileName = "static/trendy-friendy-frontend.js"
}
