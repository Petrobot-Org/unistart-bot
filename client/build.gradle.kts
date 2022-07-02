plugins {
    kotlin("multiplatform")
}

group = "ru.spbstu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
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
                implementation("dev.inmo:tgbotapi.webapps:${Versions.telegramBotApi}")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-redux")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-redux")
            }
        }
        val jsTest by getting
    }
}

dependencies {
    "jsMainImplementation"(enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:${Versions.kotlinWrappers}"))
}
