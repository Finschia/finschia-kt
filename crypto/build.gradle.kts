plugins {
    id("network.link.ln.v2.kotlin-library-conventions")
}

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
