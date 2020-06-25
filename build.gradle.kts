import org.apache.tools.ant.filters.ReplaceTokens
import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("java-library")
}

group = "ru.kdev"
version = "1.2.0-SNAPSHOT"

fun getGitHash(): String {
    val output = ByteArrayOutputStream()

    exec {
        commandLine = listOf("git", "rev-parse", "--short=8", "HEAD")
        standardOutput = output
    }

    return output.toString().replace(Regex("[\r\n ]+"), "")
}

val revision = getGitHash()

repositories {
    mavenLocal()
    mavenCentral()

    maven {
        setUrl("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
        }
    }

    maven {
        setUrl("https://repo.codemc.org/repository/maven-public/")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.3.1")
    compileOnly("mysql:mysql-connector-java:5.1.49")
}

tasks {
    clean {
        delete(files("${rootDir}/output"))
    }

    processResources {
        filter<ReplaceTokens>("tokens" to
                mapOf(
                        "plugin.version" to project.version,
                        "plugin.revision" to revision
                )
        )
    }

    compileJava {
        options.encoding = "utf-8"
    }

    jar {
        archiveFileName.set("${project.name}.jar")
        destinationDirectory.set(file("$rootDir/output"))
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}