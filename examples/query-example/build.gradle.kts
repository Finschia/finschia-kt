plugins {
    id("network.finschia.sdk.kotlin-application-conventions")
}

dependencies {
    runtimeOnly(libs.grpc.netty)

    // Finschia sdk
    implementation("network.finschia:finschia-kt-crypto:0.2.2")
    implementation("network.finschia:finschia-proto:2.0.0")

    implementation(project(":tx"))
}

application {
    mainClass.set("network.finschia.sdk.example.AccountQueryClient")
}
