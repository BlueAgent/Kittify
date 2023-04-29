@file:Suppress("PropertyName")

import net.minecraftforge.gradle.user.UserBaseExtension

val mod_id: String by project
val mod_version: String by project
val mc_version: String by project
val mc_version_short: String by project
val forge_version: String by project
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
    id("net.minecraftforge.gradle.forge")
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

configure<UserBaseExtension> {
    version = "${mc_version}-${forge_version}"
    runDir = "run"

    mappings = mappings_version
    makeObfSourceJar = false

    replaceIn("src/main/java/kittify/Kittify.java")
    replace("MOD_VERSION = \"99999.999.999\"", "MOD_VERSION = \"${mod_version}\"")
    replace("MC_VERSION = \"\"", "MC_VERSION = \"[${mc_version}]\"")
    replace("DEPENDENCIES = \"\"", "DEPENDENCIES = \"required-after:forge@[${forge_version},);after:applecore@[${applecore_version},);\"")
}

dependencies {
    // compile against the JEI API
    add("deobfCompile", "mezz.jei:jei_${mc_version}:${jei_version}:api")
    // at runtime, use the full JEI jar
    runtimeOnly("mezz.jei:jei_${mc_version}:${jei_version}")
    //add("deobfCompile", myResolver.resolve("${grimoireofgaia_projectid}", "${grimoireofgaia_fileid}"))
    //add("deobfCompile", myResolver.resolve("${mca_projectid}", "${mca_fileid}"))
    //add("deobfCompile", myResolver.resolve("${millenair_projectid}", "${millenair_fileid}"))
    //add("deobfCompile", myResolver.resolve("${nevermine_projectid}", "${nevermine_fileid}"))

    // Grimoire of Gaia
    add("deobfCompile", ":GrimoireOfGaia3-${mc_version}-${grimoireofgaia_version}:")

    // Millenaire
    add("deobfCompile", ":millenaire-${mc_version}-${millenair_version}:")

    // Minecraft Comes Alive
    add("deobfCompile", ":MCA-${mc_version}-${mca_version}-universal:")

    // Nevermine (Advent of Ascension)
    add("deobfCompile", ":AoA3-${nevermine_version}:")

    // Angel of Vengeance
    add("deobfCompile", ":TamModized-${mc_version}-${tammodized_version}:")
    add("deobfCompile", ":AoV-${mc_version_short}-${aov_version}:")

    // AppleCore
    add("deobfCompile", "applecore:AppleCore:${mc_version}-${applecore_version}:api")
    //add("deobfCompile", "applecore:AppleCore:${mc_version}-${applecore_version}:deobf")

    // AppleSkin
    add("deobfCompile", ":AppleSkin-mc${mc_version_short}-${appleskin_version}:")
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

tasks.named<JavaExec>("runClient") {
    args("--username", "KittifyDev")
}
