plugins {
    `java-library`
    idea
    `maven-publish`
    alias(libs.plugins.userdev)
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}

val mod_version: String by project
val mod_group_id: String by project
val mod_id: String by project
val mod_name: String by project
val mod_license: String by project
val mod_authors: String by project
val mod_description: String by project
val mod_credits: String by project

val minecraftVersion = libs.versions.minecraft.get()
val minecraftVersionRange = libs.versions.minecraftRange.get()
val neoVersion = libs.versions.neoforge.get()
val neoVersionRange = libs.versions.neoforgeRange.get()
val loaderVersionRange = libs.versions.loaderRange.get()

val version = "$minecraftVersion-$mod_version"

repositories {
    mavenLocal()
}

base {
    archivesName.set(mod_id)
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

minecraft.accessTransformers.file(rootProject.file("src/main/resources/META-INF/accesstransformer.cfg"))

runs {
    create("client") {
        client()
        systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
    }

    create("server") {
        server()
        argument("--nogui")
        systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
    }

    create("gameTestServer") {
        systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
    }

    create("data") {
        arguments.addAll(
            "--mod",
            mod_id,
            "--all",
            "--output",
            file("src/generated/resources/").absolutePath,
            "--existing",
            file("src/main/resources/").absolutePath,
        )
    }

    configureEach {
        systemProperty("forge.logging.markers", "REGISTRIES")
        systemProperty("forge.logging.console.level", "debug")

        modSource(project.sourceSets.main.get())
    }
}

sourceSets.main
    .get()
    .resources
    .srcDir("src/generated/resources")

configurations {
    runtimeClasspath.get().extendsFrom(localRuntime.get())
}

dependencies {
    implementation(libs.neo.neoforge)
    compileOnly(libs.bundles.jeiApi)
    localRuntime(libs.jei)
}

tasks.withType(ProcessResources::class).configureEach {
    val replaceProperties =
        mapOf(
            "minecraft_version" to minecraftVersion,
            "minecraft_version_range" to minecraftVersionRange,
            "neo_version" to neoVersion,
            "neo_version_range" to neoVersionRange,
            "loader_version_range" to loaderVersionRange,
            "mod_id" to mod_id,
            "mod_name" to mod_name,
            "mod_license" to mod_license,
            "mod_version" to mod_version,
            "mod_authors" to mod_authors,
            "mod_description" to mod_description,
            "mod_credits" to mod_credits,
        )
    inputs.properties(replaceProperties)

    filesMatching(mutableListOf("META-INF/neoforge.mods.toml")) {
        expand(replaceProperties)
    }
}

val sourcesJar =
    tasks.register("sourcesJar", Jar::class) {
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        from(sourceSets.main.get().allSource)
        archiveClassifier.set("sources")
    }

val deobfJar =
    tasks.register("deobfJar", Jar::class) {
        from(sourceSets.main.get().output)
        archiveClassifier.set("deobf")
    }

artifacts {
    archives(sourcesJar)
    archives(deobfJar)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("file://${project.projectDir}/repo")
        }
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

repositories {
    maven(url = "https://maven.blamejared.com/") { name = "Jared's maven" }
    maven(url = "https://modmaven.dev") { name = "ModMaven" }
}
