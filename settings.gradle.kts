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
        //Material CalenderView와 PieChart를 위한 import
        maven { url = uri("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        //Material CalenderView와 PieChart를 위한 import
        maven { url = uri("https://jitpack.io") }

        //카카오 소셜 로그인
        maven("https://devrepo.kakao.com/nexus/content/groups/public/")

    }
}

rootProject.name = "HwaRoak"
include(":app")
