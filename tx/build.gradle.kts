plugins {
    id("network.finschia.sdk.kotlin-library-conventions")
    kotlin("plugin.serialization") version "1.5.30"
}

// Dependency versions
val bitcoinjVersion = "0.15.6"
val kotlinxVersion = "1.2.0"

dependencies {
    implementation("org.bitcoinj:bitcoinj-core:$bitcoinjVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxVersion")

    // Finschia sdk
    implementation("network.finschia:finschia-kt-crypto:0.2.2")
    implementation("network.finschia:finschia-proto:4.0.0")
}
