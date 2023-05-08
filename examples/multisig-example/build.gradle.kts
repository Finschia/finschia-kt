plugins {
    id("network.finschia.sdk.kotlin-application-conventions")
    kotlin("plugin.serialization") version "1.5.30"
}

val bitcoinjVersion = "0.15.6"
val kotlinxVersion = "1.2.0"

dependencies {
    implementation("network.finschia:finschia-kt-crypto:0.2.2")
    implementation("network.finschia:finschia-proto:1.0.1")
    implementation(project(":tx"))
    implementation("org.bitcoinj:bitcoinj-core:$bitcoinjVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxVersion")
    runtimeOnly(libs.grpc.netty)
}

application {
    mainClass.set("network.finschia.sdk.example.ClientKt")
}
