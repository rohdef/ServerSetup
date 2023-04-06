allprojects {
    version = "1.0-SNAPSHOT"

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/rohdef/rfpath")
            credentials {
                username = project.findProperty("github_packages.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("github_packages.key") as String? ?: System.getenv("TOKEN")
            }
        }

        mavenCentral()
    }
}

subprojects {
}