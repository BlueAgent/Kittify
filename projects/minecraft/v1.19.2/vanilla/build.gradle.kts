@file:Suppress("PropertyName")

val enabled_platforms: String by project
val quilt_loader_version: String by project
val trinkets_version: String by project
val curios_version: String by project

architectury {
    common(enabled_platforms.split(","))
}

loom {
    accessWidenerPath.set(file("src/main/resources/kittify-vanilla.accesswidener"))
}

dependencies {
    modImplementation("org.quiltmc:quilt-loader:${quilt_loader_version}")
    modCompileOnly("dev.emi:trinkets:${trinkets_version}")
    modCompileOnly("top.theillusivec4.curios:curios-forge:${curios_version}:api")
}
