plugins {
    id("network.link.ln.v2.kotlin-library-conventions")
    kotlin("plugin.serialization") version "1.5.30"
}

val bitcoinjVersion = "0.15.6"
val kotlinxVersion = "1.2.0"

dependencies {
    implementation(project(":crypto"))
    api(project(":protobuf"))

    implementation("org.bitcoinj:bitcoinj-core:$bitcoinjVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxVersion")
}
