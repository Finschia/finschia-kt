plugins {
    id("network.finschia.sdk.kotlin-library-conventions")
}

// Dependency versions
val guavaVersion = "28.1-jre"
val commonsIOVersion = "2.6"
val bouncycastleVersion = "1.64"
val bcryptVersion = "0.9.0"
val xsalsa20poly1305Version = "0.11.0"
val jacksonVersion = "2.10.1"

val bitcoinjVersion = "0.15.6"
val web3jVersion = "4.5.14"

val mockitoVersion = "3.3.0"
val jupiterVersion = "5.5.2"
val truthVersion = "1.0.1"

dependencies {
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("commons-io:commons-io:$commonsIOVersion")
    implementation("org.bouncycastle:bcprov-jdk15on:$bouncycastleVersion")
    implementation("org.bouncycastle:bcpg-jdk15on:$bouncycastleVersion")
    implementation("com.codahale:xsalsa20poly1305:$xsalsa20poly1305Version")
    implementation("at.favre.lib:bcrypt:$bcryptVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("org.bitcoinj:bitcoinj-core:$bitcoinjVersion")
    implementation("org.web3j:crypto:$web3jVersion")

    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito:mockito-inline:$mockitoVersion")
    testImplementation("com.google.truth:truth:$truthVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
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
    val groupIdVal = "network.finschia"
    val artifactIdVal = "finschia-kt-crypto"
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
