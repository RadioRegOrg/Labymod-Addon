repositories {
    mavenCentral()
}
version = "0.1.0"

plugins {
    id("java-library")
}

dependencies {
    api(project(":api"))

    maven(mavenCentral(), "com.googlecode.soundlibs:mp3spi:1.9.5-2")
    maven(mavenCentral(), "com.googlecode.soundlibs:jlayer:1.0.1-2")
    maven(mavenCentral(), "com.googlecode.soundlibs:tritonus-share:0.3.7-3")
    maven(mavenCentral(), "io.socket:socket.io-client:2.1.0")
    maven(mavenCentral(), "io.socket:engine.io-client:2.1.0")
    maven(mavenCentral(), "org.json:json:20231013")
    maven(mavenCentral(), "com.squareup.okhttp3:okhttp:4.12.0")
    maven(mavenCentral(), "com.squareup.okio:okio:3.9.0")
    maven(mavenCentral(), "com.squareup.okio:okio-jvm:3.9.0")
    maven(mavenCentral(), "javazoom:jlayer:1.0.1")
}

labyModProcessor {
    referenceType = net.labymod.gradle.core.processor.ReferenceType.DEFAULT
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}