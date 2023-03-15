## How to use local jar

1. Create libs folder under app directory and place jar files.
2. Add below two paragraphs in `build.gradle.kts`
```
repositories {
    flatDir {
        dirs("libs")
    }
}
```

This paragraph tells Gradle to use filesystem as dependency store 
and adds a directory as entrypoint to look for to find dependencies.

```
dependencies {
    implementation(fileTree("$projectDir/libs") {
        include("*.jar")
    })
}
```
This paragraph adds all jar files under libs as dependencies.