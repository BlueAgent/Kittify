@file:Suppress("PropertyName")

val enabled_platforms: String by project
val quilt_loader_version: String by project
val fabric_loader_version: String by project

architectury {
    common(enabled_platforms.split(","))
}

dependencies {
    modImplementation("org.quiltmc:quilt-loader:${quilt_loader_version}")
}
