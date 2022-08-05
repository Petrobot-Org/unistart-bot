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
}

dependencies {
    implementation(project(":common"))
    implementation(libs.tgbotapi)
    implementation(libs.bundles.ktor.server)
    implementation(libs.kotlinx.html)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.logging)
    implementation(libs.sqldelight.sqlite)
    implementation(libs.kaml)
    implementation(libs.bundles.poi)
    implementation(libs.quartz)
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
        sourceFolders = listOf("sqldelight")
        verifyMigrations = true
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
