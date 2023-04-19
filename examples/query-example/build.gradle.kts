plugins {
    id("network.finschia.sdk.kotlin-application-conventions")
}

dependencies {
    implementation(project(":tx"))
    implementation(project(":crypto"))
    implementation(project(":protobuf"))
    runtimeOnly(libs.grpc.netty)
}

application {
    mainClass.set("network.finschia.sdk.example.AccountQueryClient")
}
