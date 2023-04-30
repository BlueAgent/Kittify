@file:Suppress("PropertyName", "UnstableApiUsage")

val minecraft_version: String by project
val forge_version: String by project

plugins {
    id ("com.github.johnrengelman.shadow")
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
    "forge"("net.minecraftforge:forge:${minecraft_version}-${forge_version}")
    "common"(project(path = vanillaPath, configuration = "namedElements")) { isTransitive = false }
    "shadowCommon"(project(path = vanillaPath, configuration = "transformProductionForge")) { isTransitive = false }
}
