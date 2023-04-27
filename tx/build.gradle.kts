plugins {
    id("network.finschia.sdk.kotlin-library-conventions")
    kotlin("plugin.serialization") version "1.5.30"
}

// Dependency versions
val bitcoinjVersion = "0.15.6"
val kotlinxVersion = "1.2.0"

dependencies {
    implementation(project(":crypto"))
    api(project(":protobuf"))

    implementation("org.bitcoinj:bitcoinj-core:$bitcoinjVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxVersion")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
publishing {
    // Maven artifact
    val groupIdVal = "io.github.finschia"
    val artifactIdVal = "finschia-kt-tx"
    val versionVal: String? = System.getProperty("VERSION")

    // Maven pom info
    val pomName = "finschia"
    val pomDesc = artifactIdVal
    val pomUrl = "https://github.com/Finschia/finschia-kt"
    val pomScmConnection = "scm:git:git://github.com/Finschia/finschia-kt.git"
    val pomDeveloperConnection = "scm:git:ssh://github.com/Finschia/finschia-kt.git"
    val pomScmUrl = "https://github.com/Finschia/finschia-kt"

    // Maven account
    val ossrhUserName = System.getenv("OSSRH_USERNAME")
    val ossrhPassword = System.getenv("OSSRH_PW")

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = groupIdVal
            artifactId = artifactIdVal
            version = versionVal?.substring(1) // without v

            from(components["java"])
            pom {
                name.set(pomName)
                description.set(pomDesc)
                url.set(pomUrl)
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("dev")
                        name.set("dev")
                        email.set("dev@finschia.org")
                    }
                }
                scm {
                    connection.set(pomScmConnection)
                    developerConnection.set(pomDeveloperConnection)
                    url.set(pomScmUrl)
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/content/repositories/releases/")
            credentials {
                username = ossrhUserName
                password = ossrhPassword
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}
