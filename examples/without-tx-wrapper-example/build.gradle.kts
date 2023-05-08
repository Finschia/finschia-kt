plugins {
    id("network.finschia.sdk.kotlin-application-conventions")
}

val bitcoinjVersion = "0.15.6"

dependencies {
    implementation("network.finschia:finschia-kt-crypto:0.2.2")
    implementation("network.finschia:finschia-proto:1.0.1")
    implementation("org.bitcoinj:bitcoinj-core:$bitcoinjVersion")

    runtimeOnly(libs.grpc.netty)
}

application {
    mainClass.set("network.finschia.sdk.example.ClientKt")
}
