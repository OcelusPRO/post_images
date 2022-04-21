plugins {
    kotlin("jvm") version "1.6.10"
}

group = "yt.ftnl"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.0.0")
    implementation("io.ktor:ktor-server-core-jvm:2.0.0")
    implementation("io.ktor:ktor-serialization-gson-jvm:2.0.0")
    implementation("io.ktor:ktor-server-mustache-jvm:2.0.0")
    implementation("io.ktor:ktor-server-http-redirect-jvm:2.0.0")
    implementation("io.ktor:ktor-server-hsts-jvm:2.0.0")
    implementation("io.ktor:ktor-server-default-headers-jvm:2.0.0")
    implementation("io.ktor:ktor-server-cors-jvm:2.0.0")
    implementation("io.ktor:ktor-server-compression-jvm:2.0.0")
    implementation("io.ktor:ktor-server-caching-headers-jvm:2.0.0")
    implementation("io.ktor:ktor-server-host-common-jvm:2.0.0")
    implementation("io.ktor:ktor-server-auto-head-response-jvm:2.0.0")
    implementation("io.ktor:ktor-server-sessions-jvm:2.0.0")
    implementation("io.ktor:ktor-server-netty-jvm:2.0.0")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("org.jetbrains.exposed:exposed-jodatime:0.37.3")
    implementation("org.jetbrains.exposed:exposed-core:0.37.3")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.37.3")
    implementation("org.jetbrains.exposed:exposed-dao:0.37.3")
    implementation("mysql:mysql-connector-java:8.0.28")
    testImplementation("io.ktor:ktor-server-tests-jvm:2.0.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.6.20")
}