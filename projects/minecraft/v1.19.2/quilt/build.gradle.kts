@file:Suppress("PropertyName", "UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

val dev_username: String by project
val dev_simulated_latency: String by project
val mod_version: String by project
val minecraft_version: String by project
val quilt_loader_version: String by project
val quilted_fabric_api_version: String by project
val trinkets_version: String by project

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
    loader("quilt")
}

loom {
    accessWidenerPath.set(project(vanillaPath).loom.accessWidenerPath)
    runs {
        named("client") {
            programArg("--username")
            programArg(dev_username)
        }
        named("server") {
            property("kittify.simulated_latency", dev_simulated_latency)
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
    named("developmentQuilt") {
        extendsFrom(common)
    }
}

dependencies {
    modImplementation("org.quiltmc:quilt-loader:${quilt_loader_version}")
    modApi("org.quiltmc.quilted-fabric-api:quilted-fabric-api:${quilted_fabric_api_version}-${minecraft_version}")
    "common"(project(path = vanillaPath, configuration = "namedElements")) { isTransitive = false }
    "shadowCommon"(project(path = vanillaPath, configuration = "transformProductionQuilt")) { isTransitive = false }
    "common"(project(path = quiltishPath, configuration = "namedElements")) { isTransitive = false }
    "shadowCommon"(project(path = quiltishPath, configuration = "transformProductionQuilt")) { isTransitive = false }
    modImplementation("dev.emi:trinkets:${trinkets_version}") {
        exclude("net.fabricmc")
        exclude("net.fabricmc.fabric-api")
    }
}

tasks.named<ProcessResources>("processResources") {
    inputs.property("mod_version", mod_version)
    inputs.property("minecraft_version", minecraft_version)
    inputs.property("quilt_loader_version", quilt_loader_version)
    inputs.property("quilted_fabric_api_version", quilted_fabric_api_version)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(sourceSets["main"].resources.srcDirs) {
        include("quilt.mod.json")
        expand(
            "mod_version" to mod_version,
            "minecraft_version" to minecraft_version,
            "quilt_loader_version" to quilt_loader_version,
            "quilted_fabric_api_version" to quilted_fabric_api_version
        )
    }
    from(sourceSets["main"].resources.srcDirs) {
        exclude("quilt.mod.json")
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
