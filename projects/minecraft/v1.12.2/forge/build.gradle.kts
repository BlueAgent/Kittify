@file:Suppress("PropertyName")

import net.kyori.blossom.BlossomExtension
import net.minecraftforge.gradle.userdev.UserDevExtension

val dev_username: String by project
val mod_id: String by project
val mod_version: String by project
val mc_version: String by project
val mc_version_short: String by project
val forge_version: String by project
val mappings_channel: String by project
val mappings_version: String by project
val jei_version: String by project
val grimoireofgaia_projectid: String by project
val grimoireofgaia_version: String by project
val grimoireofgaia_fileid: String by project
val millenair_projectid: String by project
val millenair_version: String by project
val millenair_fileid: String by project
val mca_projectid: String by project
val mca_version: String by project
val mca_fileid: String by project
val nevermine_projectid: String by project
val nevermine_version: String by project
val nevermine_fileid: String by project
val tammodized_projectid: String by project
val tammodized_version: String by project
val tammodized_fileid: String by project
val aov_projectid: String by project
val aov_version: String by project
val aov_fileid: String by project
val applecore_version: String by project
val appleskin_version: String by project

plugins {
    id("com.github.ben-manes.versions")
    id("net.kyori.blossom")
    id("net.minecraftforge.gradle")
}

repositories {
    maven {
        // JEI
        url = uri("https://dvs1.progwml6.com/files/maven")
        content {
            includeGroup("mezz.jei")
        }
    }
    maven {
        // AppleCore, AppleSkin
        url = uri("https://www.ryanliptak.com/maven/")
        content {
            includeGroup("applecore")
        }
    }
    flatDir {
        dirs("libs")
        content {
            includeGroup("libs")
        }
    }
}

base.archivesName.set("${mod_id}-${mc_version}")

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

// Workaround for the source and target compatibility being set by something...
@Suppress("NULL_FOR_NONNULL_TYPE")
configure<JavaPluginExtension> {
    sourceCompatibility = null
    targetCompatibility = null
}

configure<UserDevExtension> {
    mappings(mappings_channel, mappings_version)
    accessTransformer(file("src/main/resources/META-INF/${mod_id}_at.cfg"))
    runs {
        all {
            lazyToken("minecraft_classpath") {
                val configurationCopy = configurations.implementation.get().copyRecursive()
                configurationCopy.isCanBeResolved = true
                configurationCopy.isTransitive = false
                configurationCopy.resolve().joinToString(File.pathSeparator) { it.absolutePath }
            }
        }
        create("client") {
            args("--username", dev_username)
            workingDirectory(file("run"))
            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
            property("forge.logging.console.level", "debug")
            mods {
                create(mod_id) {
                    sources = listOf(sourceSets["main"])
                }
            }
        }
        create("server") {
            workingDirectory(file("run"))
            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
            property("forge.logging.console.level", "debug")
            mods {
                create(mod_id) {
                    sources = listOf(sourceSets["main"])
                }
            }
        }
    }
}

dependencies {
    add("minecraft", "net.minecraftforge:forge:${mc_version}-${forge_version}")
    // compile against the JEI API
    compileOnly(fg.deobf("mezz.jei:jei_${mc_version}:${jei_version}:api"))
    // at runtime, use the full JEI jar
    runtimeOnly("mezz.jei:jei_${mc_version}:${jei_version}")

    // Grimoire of Gaia
    implementation(fg.deobf("libs:GrimoireOfGaia3:${mc_version}-${grimoireofgaia_version}"))

    // Millenaire
    implementation(fg.deobf("libs:millenaire:${mc_version}-${millenair_version}"))

    // Minecraft Comes Alive
    implementation(fg.deobf("libs:MCA:${mc_version}-${mca_version}-universal"))

    // Nevermine (Advent of Ascension)
    implementation(fg.deobf("libs:AoA3:${nevermine_version}"))

    // Angel of Vengeance
    implementation(fg.deobf("libs:TamModized:${mc_version}-${tammodized_version}"))
    implementation(fg.deobf("libs:AoV:${mc_version_short}-${aov_version}"))

    // AppleCore
    implementation(fg.deobf("applecore:AppleCore:${mc_version}-${applecore_version}:api"))
    //implementation(fg.deobf("applecore:AppleCore:${mc_version}-${applecore_version}:deobf"))

    // AppleSkin
    implementation(fg.deobf("libs:AppleSkin:mc${mc_version_short}-${appleskin_version}"))
}

configure<BlossomExtension> {
    replaceToken("MOD_VERSION = \"99999.999.999\"", "MOD_VERSION = \"${mod_version}\"")
    replaceToken("MC_VERSION = \"\"", "MC_VERSION = \"[${mc_version}]\"")
    replaceToken("DEPENDENCIES = \"\"", "DEPENDENCIES = \"required-after:forge@[${forge_version},);after:applecore@[${applecore_version},);\"")
    replaceTokenIn("src/main/java/kittify/Kittify.java")
}

tasks.named<ProcessResources>("processResources") {
    inputs.property("mod_version", mod_version)
    inputs.property("mc_version", mc_version)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(sourceSets["main"].resources.srcDirs) {
        include("mcmod.info")
        expand(
            "mod_version" to mod_version,
            "mc_version" to mc_version,
        )
    }
    from(sourceSets["main"].resources.srcDirs) {
        exclude("mcmod.info")
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "FMLAT" to "${mod_id}_at.cfg",
            "FMLCorePlugin" to "kittify.core.KittifyCore",
            "FMLCorePluginContainsFMLMod" to "true",
        )
    }
}
