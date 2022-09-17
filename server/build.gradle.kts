plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
    application
}

group = "ru.spbstu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.repsy.io/mvn/ithersta/tgbotapi") }
}

dependencies {
    implementation(project(":common"))
    implementation(libs.tgbotapi)
    implementation(libs.tgbotapi.fsm)
    implementation(libs.bundles.ktor.server)
    implementation(libs.kotlinx.html)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.logging)
    implementation(libs.postgresql)
    implementation(libs.hikari)
    implementation(libs.sqldelight.jdbc)
    implementation(libs.kaml)
    implementation(libs.bundles.poi)
    implementation(libs.quartz)
    implementation(libs.serialization.protobuf)
    implementation(libs.ktor.client.okhttp)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("ru.spbstu.application.MainKt")
}

sqldelight {
    database("AppDatabase") {
        packageName = "ru.spbstu.application.data.source"
        dialect("app.cash.sqldelight:postgresql-dialect:2.0.0-alpha03")
        sourceFolders = listOf("sqldelight")
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}

tasks.named<Jar>("jar") {
    dependsOn(":trendy-friendy-frontend:jsBrowserProductionWebpack")
    from("${rootProject.projectDir}/trendy-friendy-frontend/build/distributions/")
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jar"))
    classpath(tasks.named<Jar>("jar"))
}
