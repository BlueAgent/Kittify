@file:Suppress("PropertyName", "UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

val minecraft_version: String by project
val quilt_loader_version: String by project
val quilted_fabric_api_version: String by project

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
