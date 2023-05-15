plugins {
    id("network.finschia.sdk.kotlin-application-conventions")
}

val bitcoinjVersion = "0.15.6"

dependencies {
    runtimeOnly(libs.grpc.netty)

    implementation("org.bitcoinj:bitcoinj-core:$bitcoinjVersion")

    // Finschia sdk
    implementation("network.finschia:finschia-kt-crypto:0.2.2")
    implementation("network.finschia:finschia-proto:1.0.1")
}

application {
    mainClass.set("network.finschia.sdk.example.ClientKt")
}
