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
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
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
