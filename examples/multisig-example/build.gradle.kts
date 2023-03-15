plugins {
    id("network.finschia.ln.v2.kotlin-application-conventions")
    kotlin("plugin.serialization") version "1.5.30"
}

val bitcoinjVersion = "0.15.6"
val kotlinxVersion = "1.2.0"

dependencies {
    implementation(project(":crypto"))
    implementation(project(":protobuf"))
    implementation(project(":tx"))
    implementation("org.bitcoinj:bitcoinj-core:$bitcoinjVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxVersion")
    runtimeOnly(libs.grpc.netty)
}

application {
    mainClass.set("network.finschia.ln.v2.example.ClientKt")
}
