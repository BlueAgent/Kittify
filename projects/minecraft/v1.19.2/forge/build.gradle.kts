@file:Suppress("PropertyName", "UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

val mod_version: String by project
val minecraft_version: String by project
val forge_loader_version: String by project
val forge_version: String by project

plugins {
    id("com.github.johnrengelman.shadow")
}

val parentPath = org.gradle.util.Path.path(project.path).parent!!
val vanillaPath = parentPath.child("vanilla").path!!
evaluationDependsOn(vanillaPath)

architectury {
    platformSetupLoomIde()
    forge()
}

configurations {
    val common = create("common")
    create("shadowCommon")
    compileClasspath.configure {
        extendsFrom(common)
    }
    runtimeClasspath.configure {
        extendsFrom(common)
    }
    named("developmentForge") {
        extendsFrom(common)
    }
}

dependencies {
    forge("net.minecraftforge:forge:${minecraft_version}-${forge_version}")
    "common"(project(path = vanillaPath, configuration = "namedElements")) { isTransitive = false }
    "shadowCommon"(project(path = vanillaPath, configuration = "transformProductionForge")) { isTransitive = false }
}

tasks.named<ProcessResources>("processResources") {
    inputs.property("mod_version", mod_version)
    inputs.property("minecraft_version", minecraft_version)
    inputs.property("forge_loader_version", forge_loader_version)
    inputs.property("forge_version", forge_version)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(sourceSets["main"].resources.srcDirs) {
        include("META-INF/mods.toml")
        expand(
            "mod_version" to mod_version,
            "minecraft_version" to minecraft_version,
            "forge_loader_version" to forge_loader_version,
            "forge_version" to forge_version
        )
    }
    from(sourceSets["main"].resources.srcDirs) {
        exclude("META-INF/mods.toml")
    }
}

tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations["shadowCommon"])
    archiveClassifier.set("dev-shadow")
}

tasks.named<RemapJarTask>("remapJar") {
    val shadowJar = tasks.named<ShadowJar>("shadowJar").get()
    inputFile.set(shadowJar.archiveFile)
    dependsOn(shadowJar)
    archiveClassifier.set(null as String?)
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("dev")
}

tasks.named<Jar>("sourcesJar") {
    val commonSources = project(vanillaPath).tasks.named<Jar>("sourcesJar").get()
    dependsOn(commonSources)
    from(commonSources.archiveFile.map { zipTree(it) })
}

components.named<AdhocComponentWithVariants>("java") {
    withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) {
        skip()
    }
}
