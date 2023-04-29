@file:Suppress("PropertyName")

val mod_id: String by project
val mod_version: String by project

allprojects {
    group = mod_id
    version = mod_version
}
