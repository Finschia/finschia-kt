import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.generateProtoTasks

plugins {
    id("network.link.ln.v2.kotlin-library-conventions")
    id("com.google.protobuf")
    id("distribution")
}

sourceSets {
    main {
        proto {
            srcDir ("./repositories/lbm-sdk/proto")
            srcDir ("./repositories/lbm-sdk/third_party/proto")
            srcDir ("./repositories/wasmd/proto")
            srcDir ("./repositories/ibc-go/proto")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.proto.get()}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.get()}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${libs.versions.grpckotlin.get()}:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions {
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}

dependencies {
    api(libs.grpc.protobuf)
    api(libs.grpc.kotlin.stub)
    api(libs.grpc.stub)
    api(libs.protobuf.kotlin)
}
