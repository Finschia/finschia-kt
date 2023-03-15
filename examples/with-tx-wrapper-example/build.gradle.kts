plugins {
    id("network.finschia.ln.v2.kotlin-application-conventions")
}

dependencies {
    implementation(project(":tx"))
    implementation(project(":crypto"))
    implementation(project(":protobuf"))
    runtimeOnly(libs.grpc.netty)
}

application {
    mainClass.set("network.finschia.ln.v2.example.ClientKt")
}
