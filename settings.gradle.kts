pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when (requested.id.toString()) {
                "com.github.ben-manes.versions" -> {
                    useModule("com.github.ben-manes:gradle-versions-plugin:0.46.0")
                }

                "com.github.johnrengelman.shadow" -> {
                    useModule("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
                }

                "net.kyori.blossom" -> {
                    useModule("net.kyori:blossom:1.3.1")
                }

                "architectury-plugin" -> {
                    useModule("architectury-plugin:architectury-plugin.gradle.plugin:3.4-SNAPSHOT")
                }

                "dev.architectury.loom" -> {
                    useModule("dev.architectury.loom:dev.architectury.loom.gradle.plugin:1.1-SNAPSHOT")
                }

                "io.github.juuxel.loom-quiltflower" -> {
                    useModule("io.github.juuxel:loom-quiltflower:1.8.0")
                }

                "net.minecraftforge.gradle" -> {
                    useModule("net.minecraftforge.gradle:ForgeGradle:5.1.73")
                }
            }
        }
    }
    repositories {
        maven {
            name = "Architectury"
            url = uri("https://maven.architectury.dev")
            content {
                includeGroupByRegex("""^architectury-plugin(?:\..+$|$)""")
                includeGroupByRegex("""^dev.architectury(?:\..+$|$)""")
            }
        }
        maven {
            name = "Quilt"
            url = uri("https://maven.quiltmc.org/repository/release")
            content {
                includeGroupByRegex("""^org.quiltmc(?:\..+$|$)""")
            }
        }
        maven {
            name = "MinecraftForge"
            url = uri("https://maven.minecraftforge.net")
            content {
                includeGroup("de.oceanlabs.mcp")
                includeGroup("net.minecraft")
                includeGroupByRegex("""^net.minecraftforge(?:\..+$|$)""")
            }
        }
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net")
            content {
                includeGroupByRegex("""^net.fabricmc(?:\..+$|$)""")
            }
        }
        gradlePluginPortal {
            content {
                includeGroup("com.github.ben-manes")
                includeGroup("io.github.juuxel")
                includeGroup("gradle.plugin.com.github.johnrengelman")
                includeGroup("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext")
                includeGroup("net.kyori")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "kittify"

include("projects:minecraft:v1.19.2")
include("projects:minecraft:v1.19.2:vanilla")
include("projects:minecraft:v1.19.2:quiltish")
include("projects:minecraft:v1.19.2:quilt")
include("projects:minecraft:v1.19.2:forge")
include("projects:minecraft:v1.19.2:fabric")

//include("projects:minecraft:v1.12.2:forge")
