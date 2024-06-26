repositories {
    maven("https://jitpack.io")
    mavenCentral()
}
version = "0.1.0"

plugins {
    id("java-library")
}

dependencies {
    api(project(":api"))

    maven(mavenCentral(), "io.socket:socket.io-client:2.1.0")
    maven(mavenCentral(), "io.socket:engine.io-client:2.1.0")
    maven(mavenCentral(), "org.json:json:20231013")
    maven(mavenCentral(), "com.squareup.okhttp3:okhttp:4.12.0")
    maven(mavenCentral(), "com.squareup.okio:okio:3.9.0")
    maven(mavenCentral(), "com.squareup.okio:okio-jvm:3.9.0")
    maven(mavenCentral(), "dev.arbjerg:lavaplayer:2.2.0")
    maven(mavenCentral(), "dev.arbjerg:lavaplayer-natives:2.2.0")
    maven(mavenCentral(), "dev.arbjerg:lava-common:2.2.0")
    maven(mavenCentral(), "org.mozilla:rhino-engine:1.7.15")
    maven(mavenCentral(), "org.mozilla:rhino:1.7.15")
}

labyModProcessor {
    referenceType = net.labymod.gradle.core.processor.ReferenceType.DEFAULT
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}