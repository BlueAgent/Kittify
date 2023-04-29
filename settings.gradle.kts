pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when (requested.id.toString()) {
                "com.github.ben-manes.versions" -> {
                    useModule("com.github.ben-manes:gradle-versions-plugin:0.46.0")
                }

                "net.kyori.blossom" -> {
                    useModule("net.kyori:blossom:1.3.1")
                }

                "net.minecraftforge.gradle" -> {
                    useModule("net.minecraftforge.gradle:ForgeGradle:5.1.73")
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
                includeGroupByRegex("^net\\.minecraftforge(?:\\..+$|$)")
            }
        }
        gradlePluginPortal {
            content {
                includeGroup("com.github.ben-manes")
                includeGroup("net.kyori")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "kittify"

include("projects:minecraft:v1.12.2:forge")
