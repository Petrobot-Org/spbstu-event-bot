import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    id("com.squareup.sqldelight") version "1.5.3"
    application
}

group = "ru.spbstu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("io.insert-koin:koin-core:3.2.0-beta-1")
    implementation("com.squareup.sqldelight:sqlite-driver:1.5.3")
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.0.6")
    implementation("commons-validator:commons-validator:1.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("io.insert-koin:koin-test:3.2.0-beta-1")
    testImplementation("io.insert-koin:koin-test-junit5:3.2.0-beta-1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += listOf("-Xcontext-receivers")
}

application {
    mainClass.set("ru.spbstu.eventbot.MainKt")
}

sqldelight {
    database("AppDatabase") {
        packageName = "ru.spbstu.eventbot.data.source"
        verifyMigrations = true
    }
}
