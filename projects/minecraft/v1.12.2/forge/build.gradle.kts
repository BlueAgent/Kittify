buildscript {
    repositories {
        jcenter()
        maven {
            url = "https://files.minecraftforge.net/maven"
        }
        maven {
            url = "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath 'net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT'
        //classpath 'com.wynprice.cursemaven:CurseMaven:1.0.0'
    }
}

apply plugin: 'net.minecraftforge.gradle.forge'
//apply plugin: 'com.wynprice.cursemaven'

//import com.wynprice.cursemaven.CurseMavenResolver

//def myResolver = new CurseMavenResolver(attachSource: true, debug: true)

repositories {
    maven {
        // JEI
        url = "http://dvs1.progwml6.com/files/maven"
    }
    maven {
        // AppleCore, AppleSkin
        url "http://www.ryanliptak.com/maven/"
    }
    flatDir {
        dirs 'libs'
    }
}

version = "${mod_version}"
group = archivesBaseName = "${mod_id}"
sourceCompatibility = targetCompatibility = JavaVersion.VERSION_1_8

minecraft {
    version = "${mc_version}-${forge_version}"
    runDir = "run"

    mappings = "${mappings_version}"
    makeObfSourceJar = false

    replaceIn "src/main/java/kittify/Kittify.java"
    replace "MOD_VERSION = \"99999.999.999\"", "MOD_VERSION = \"${mod_version}\""
    replace "MC_VERSION = \"\"", "MC_VERSION = \"[${mc_version}]\""
    replace "DEPENDENCIES = \"\"", "DEPENDENCIES = \"required-after:forge@[${forge_version},);after:applecore@[${applecore_version},);\""
}

dependencies {
    testCompile "junit:junit:${junit_version}"

    // compile against the JEI API
    deobfCompile "mezz.jei:jei_${mc_version}:${jei_version}:api"
    // at runtime, use the full JEI jar
    runtime "mezz.jei:jei_${mc_version}:${jei_version}"
    //deobfCompile myResolver.resolve("${grimoireofgaia_projectid}", "${grimoireofgaia_fileid}")
    //deobfCompile myResolver.resolve("${mca_projectid}", "${mca_fileid}")
    //deobfCompile myResolver.resolve("${millenair_projectid}", "${millenair_fileid}")
    //deobfCompile myResolver.resolve("${nevermine_projectid}", "${nevermine_fileid}")

    // Grimoire of Gaia
    deobfCompile name: "GrimoireOfGaia3-${mc_version}-${grimoireofgaia_version}"

    // Millenaire
    deobfCompile name: "millenaire-${mc_version}-${millenair_version}"

    // Minecraft Comes Alive
    deobfCompile name: "MCA-${mc_version}-${mca_version}-universal"

    // Nevermine (Advent of Ascension)
    deobfCompile name: "AoA3-${nevermine_version}"

    // Angel of Vengeance
    deobfCompile name: "TamModized-${mc_version}-${tammodized_version}"
    deobfCompile name: "AoV-${mc_version_short}-${aov_version}"

    // AppleCore
    deobfCompile "applecore:AppleCore:${mc_version}-${applecore_version}:api"
    //deobfCompile "applecore:AppleCore:${mc_version}-${applecore_version}:deobf"

    // AppleSkin
    deobfCompile name: "AppleSkin-mc${mc_version_short}-${appleskin_version}"
}

processResources {
    // this will ensure that this task is redone when the versions change.
    inputs.property "version", project.version
    inputs.property "mcversion", project.minecraft.version

    // replace stuff in mcmod.info, nothing else
    from(sourceSets.main.resources.srcDirs) {
        include 'mcmod.info'

        // replace version and mcversion
        expand 'version': project.version, 'mcversion': project.minecraft.version
    }

    // copy everything else except the mcmod.info
    from(sourceSets.main.resources.srcDirs) {
        exclude 'mcmod.info'
    }
}

jar {
    manifest {
        attributes 'FMLAT': "${mod_id}_at.cfg"
        attributes 'FMLCorePlugin': 'kittify.core.KittifyCore'
        attributes 'FMLCorePluginContainsFMLMod': 'true'
    }
}

runClient {
    args '--username', 'KittifyDev'
}