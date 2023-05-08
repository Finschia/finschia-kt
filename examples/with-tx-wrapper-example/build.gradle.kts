plugins {
    id("network.finschia.sdk.kotlin-application-conventions")
}

dependencies {
    implementation(project(":tx"))
    implementation("network.finschia:finschia-kt-crypto:0.2.2")
    implementation("network.finschia:finschia-proto:1.0.1")
    runtimeOnly(libs.grpc.netty)
}

application {
    mainClass.set("network.finschia.sdk.example.ClientKt")
}
