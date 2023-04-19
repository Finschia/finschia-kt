plugins {
    id("network.finschia.sdk.kotlin-application-conventions")
}

val bitcoinjVersion = "0.15.6"

dependencies {
    implementation(project(":crypto"))
    implementation(project(":protobuf"))
    implementation("org.bitcoinj:bitcoinj-core:$bitcoinjVersion")

    runtimeOnly(libs.grpc.netty)
}

application {
    mainClass.set("network.finschia.sdk.example.ClientKt")
}
