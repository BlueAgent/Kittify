@file:Suppress("PropertyName", "UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

val dev_username: String by project
val mod_version: String by project
val minecraft_version: String by project
val fabric_loader_version: String by project
val fabric_api_version: String by project

plugins {
    id("com.github.johnrengelman.shadow")
}

val parentPath = org.gradle.util.Path.path(project.path).parent!!
val vanillaPath = parentPath.child("vanilla").path!!
evaluationDependsOn(vanillaPath)
val quiltishPath = parentPath.child("quiltish").path!!
evaluationDependsOn(quiltishPath)

architectury {
    platformSetupLoomIde()
    loader("fabric")
}

loom {
    accessWidenerPath.set(project(vanillaPath).loom.accessWidenerPath)
    runs {
        named("client") {
            programArg("--username")
            programArg(dev_username)
        }
    }
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
    named("developmentFabric") {
        extendsFrom(common)
    }
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${fabric_loader_version}")
    modApi("net.fabricmc.fabric-api:fabric-api:${fabric_api_version}")
    "common"(project(path = vanillaPath, configuration = "namedElements")) { isTransitive = false }
    "shadowCommon"(project(path = vanillaPath, configuration = "transformProductionFabric")) { isTransitive = false }
    "common"(project(path = quiltishPath, configuration = "namedElements")) { isTransitive = false }
    "shadowCommon"(project(path = quiltishPath, configuration = "transformProductionFabric")) { isTransitive = false }
}

tasks.named<ProcessResources>("processResources") {
    inputs.property("mod_version", mod_version)
    inputs.property("minecraft_version", minecraft_version)
    inputs.property("fabric_loader_version", fabric_loader_version)
    inputs.property("fabric_api_version", fabric_api_version)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(sourceSets["main"].resources.srcDirs) {
        include("fabric.mod.json")
        expand(
            "mod_version" to mod_version,
            "minecraft_version" to minecraft_version,
            "fabric_loader_version" to fabric_loader_version,
            "fabric_api_version" to fabric_api_version
        )
    }
    from(sourceSets["main"].resources.srcDirs) {
        exclude("fabric.mod.json")
    }
}

tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations["shadowCommon"])
    archiveClassifier.set("dev-shadow")
}

tasks.named<RemapJarTask>("remapJar") {
    injectAccessWidener.set(true)
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
