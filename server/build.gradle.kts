plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.squareup.sqldelight") version Versions.sqlDelight
    application
}

group = "ru.spbstu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation("dev.inmo:tgbotapi:${Versions.telegramBotApi}")
    implementation("io.ktor:ktor-server-netty:${Versions.ktor}")
    implementation("io.ktor:ktor-server-html-builder-jvm:${Versions.ktor}")
    implementation("io.ktor:ktor-server-auth:${Versions.ktor}")
    implementation("io.ktor:ktor-server-content-negotiation:${Versions.ktor}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${Versions.ktor}")
    implementation("io.ktor:ktor-server-compression:${Versions.ktor}")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:${Versions.kotlinxHtml}")
    implementation("io.insert-koin:koin-core:${Versions.koin}")
    implementation("io.insert-koin:koin-ktor:${Versions.koin}")
    implementation("io.insert-koin:koin-logger-slf4j:${Versions.koin}")
    implementation("org.slf4j:slf4j-simple:${Versions.slf4jSimple}")
    implementation("com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}")
    implementation("com.squareup.sqldelight:coroutines-extensions-jvm:${Versions.sqlDelight}")
    implementation("com.charleskorn.kaml:kaml:${Versions.kaml}")
    implementation("org.apache.poi:poi:${Versions.poi}")
    implementation("org.apache.poi:poi-ooxml:${Versions.poi}")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("ru.spbstu.application.ServerKt")
}

sqldelight {
    database("AppDatabase") {
        packageName = "ru.spbstu.application.data.source"
        sourceFolders = listOf("sqldelight")
        verifyMigrations = true
    }
}

tasks.named<Jar>("jar") {
    dependsOn(":trendy-friendy-frontend:jsBrowserProductionWebpack")
    from("${rootProject.projectDir}/trendy-friendy-frontend/build/distributions/")
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jar"))
    classpath(tasks.named<Jar>("jar"))
}
