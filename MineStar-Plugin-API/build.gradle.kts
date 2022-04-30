plugins {
    id("org.jetbrains.dokka") version "1.6.10"
    `maven-publish`
}

group = "kr.kro.minestar"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
    maven("https://repo.projecttl.net/repository/maven-public/")
}

dependencies {
//    compileOnly(project(":MineStar-Plugin"))
}

tasks {
    javadoc {
        options.encoding = "UTF-8"
    }

    create<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }

    create<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        dependsOn("dokkaHtml")
        from("$buildDir/dokka/html")
    }
}

publishing {
    publications {
        create<MavenPublication>("${rootProject.name}-api") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            repositories {
                maven {
                    name = "MavenCentral"
                    val releasesRepoUrl = "https://repo.projecttl.net/repository/maven-releases/"
                    val snapshotsRepoUrl = "https://repo.projecttl.net/repository/maven-snapshots/"
                    url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

                    credentials.runCatching {
                        username = project.properties["username"] as String?
                        password = project.properties["password"] as String?
                    }
                }

                pom {
                    val repository = rootProject.name
                    name.set(repository)
                    description.set("This is MineStar's $repository plugin")
                    url.set("https://github.com/MineStarAS/$repository")
                    licenses {
                        license {
                            name.set("GNU GENERAL PUBLIC LICENSE Version 3")
                            url.set("https://www.gnu.org/licenses/gpl-3.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("MineStarAS")
                            name.set("MineStar")
                            email.set("band1019@naver.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:https://github.com/MineStarAS/$repository.git")
                        developerConnection.set("scm:git:https://github.com/MineStarAS/$repository.git")
                        url.set("https://github.com/MineStarAS/$repository.git")
                    }
                }
            }
        }
    }
}