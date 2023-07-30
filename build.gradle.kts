@file:Suppress("PropertyName")

val mod_id: String by project
val mod_version: String by project

allprojects {
    group = mod_id
    version = mod_version

    repositories {
        maven {
            name = "TerraformersMC"
            url = uri("https://maven.terraformersmc.com")
            content {
                includeGroupByRegex("""^dev\.emi(?:\..+$|$)""")
            }
        }
        maven {
            name = "Ladysnake Libs"
            url = uri("https://maven.ladysnake.org/releases")
            content {
                includeGroupByRegex("""^dev\.emi(?:\..+$|$)""")
                includeGroupByRegex("""^dev\.onyxstudios(?:\..+$|$)""")
            }
        }
        maven {
            name = "TheIllusiveC4"
            url = uri("https://maven.theillusivec4.top")
            content {
                includeGroupByRegex("""^top\.theillusivec4(?:\..+$|$)""")
            }
        }
    }
}
