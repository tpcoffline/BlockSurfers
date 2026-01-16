plugins {
    `java-library`
}

allprojects {
    group = "com.blocksurfers"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://jitpack.io") // Minestom genelde buradan çekilir
        maven("https://repo.papermc.io/repository/maven-public/") // Velocity için
    }
}

subprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25)) // Java 25 Şartı
        }
    }

    // Minestom ve modern Java özellikleri için preview flagleri gerekebilir
    tasks.withType<JavaCompile> {
        options.compilerArgs.add("--enable-preview")
    }
}