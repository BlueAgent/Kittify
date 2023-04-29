pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when (requested.id.toString()) {
                "com.github.ben-manes.versions" -> {
                    useModule("com.github.ben-manes:gradle-versions-plugin:0.46.0")
                }

                "net.minecraftforge.gradle.forge" -> {
                    useModule("com.anatawa12.forge:ForgeGradle:2.3-1.0.8")
                }
            }
        }
    }
    repositories {
        maven {
            name = "MinecraftForge"
            url = uri("https://maven.minecraftforge.net")
            content {
                includeGroup("de.oceanlabs.mcp")
                includeGroup("net.minecraft")
                includeGroupByRegex("^net\\.minecraftforge(?:\\.|$)")
            }
        }
        gradlePluginPortal {
            content {
                includeGroup("com.github.ben-manes")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "kittify"

include("projects:minecraft:v1.12.2:forge")
