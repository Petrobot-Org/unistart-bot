plugins {
    kotlin("multiplatform") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
    id("com.squareup.sqldelight") version Versions.sqlDelight
    application
}

group = "ru.spbstu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "16"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.insert-koin:koin-core:${Versions.koin}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}")
                implementation("com.squareup.sqldelight:runtime:${Versions.sqlDelight}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("dev.inmo:tgbotapi:${Versions.telegramBotApi}")
                implementation("io.ktor:ktor-server-netty:${Versions.ktor}")
                implementation("io.ktor:ktor-server-html-builder-jvm:${Versions.ktor}")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${Versions.kotlinxHtml}")
                implementation("io.insert-koin:koin-ktor:${Versions.koin}")
                implementation("io.insert-koin:koin-logger-slf4j:${Versions.koin}")
                implementation("org.slf4j:slf4j-simple:${Versions.slf4jSimple}")
                implementation("com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}")
                implementation("com.squareup.sqldelight:coroutines-extensions-jvm:${Versions.sqlDelight}")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
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

application {
    mainClass.set("ru.spbstu.application.ServerKt")
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}

sqldelight {
    database("AppDatabase") {
        packageName = "ru.spbstu.unistart-bot.data.entities"
        sourceFolders = listOf("sqldelight")
        verifyMigrations = true
    }
}