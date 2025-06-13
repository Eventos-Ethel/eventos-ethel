pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
<<<<<<< HEAD
        maven ( url = "https://jitpack.io" )
    }
}

rootProject.name = "RegistroEventosEthel"
=======
    }
}

rootProject.name = "Trabajo DEVOPS"
>>>>>>> 2166c9ec5344b64a51ed10bd1d8f5174947e46c1
include(":app")
 