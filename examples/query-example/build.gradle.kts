plugins {
    id("network.link.ln.v2.kotlin-application-conventions")
}

dependencies {
    implementation(project(":tx"))
    implementation(project(":crypto"))
    implementation(project(":protobuf"))
    runtimeOnly(libs.grpc.netty)
}

application {
    mainClass.set("network.link.ln.v2.example.AccountQueryClient")
}
