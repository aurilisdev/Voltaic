pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven(url = "https://maven.neoforged.net/releases")
        // location of the maven that hosts JEI files since January 2023
        maven(url = "https://maven.blamejared.com/") { name = "Jared's maven" }
        // location of a maven mirror for JEI files, as a fallback
        maven(url = "https://modmaven.dev") { name = "ModMaven" }
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://cursemaven.com")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    versionCatalogs.create("libs") {
        from(files("libs.versions.toml"))
    }
}
