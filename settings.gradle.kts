rootProject.name = "finschia-kt"

pluginManagement {
    plugins {
        id("com.google.protobuf") version "0.8.17"
    }
}

enableFeaturePreview("VERSION_CATALOGS")
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

include(
    "protobuf",
    "crypto",
    "tx",
)

include(
    "examples:multisig-example",
    "examples:with-tx-wrapper-example",
    "examples:without-tx-wrapper-example",
    "examples:query-example"
)
