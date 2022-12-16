import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.8.0-Beta"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

defaultTasks("clean", "build")

repositories {
    //CodeMc
    maven ("https://repo.codemc.org/repository/maven-public/")
    //WorldGuard
    maven ("https://maven.enginehub.org/repo/")
    //PlaceHolderAPI
    maven ("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    //PaperMC
    maven ("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    implementation(project(":wgrp-api"))
    //HikariCP
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.bstats:bstats-bukkit:3.0.0")
    //MariaDB for DataBase
    implementation("org.mariadb.jdbc:mariadb-java-client:3.0.6")
    //Inject
    implementation("com.google.inject:guice:5.1.0")
    implementation("com.google.inject.extensions:guice-assistedinject:5.1.0")

    //Annotations
    compileOnly("org.jetbrains:annotations:23.0.0")

    implementation("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    implementation("org.projectlombok:lombok:1.18.24")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.24")

    compileOnly("net.kyori:adventure-api:4.12.0")
    implementation("net.kyori:adventure-text-minimessage:4.12.0")
    //PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.11.2")
    //WorldGuard 7+
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7")
    //Paper 1.19
    compileOnly(dependencyNotation = "io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))

}

tasks {
    // Configure reobfJar to run when invoking the build task
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    jar {
        archiveFileName.set("${rootProject.name}-bukkit-no-shade-${rootProject.version}.${archiveExtension.getOrElse("jar")}")
    }
    shadowJar {
        archiveFileName.set("${rootProject.name}-bukkit-${rootProject.version}.${archiveExtension.getOrElse("jarinjar")}")
    }
    build {
        dependsOn(shadowJar)
    }
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "version" to project.version,
                "group" to project.group,
                "author" to project.property("author"),
                "description" to project.property("description"),
            )
        }
    }
    shadowJar {
        //Shaded components
        relocate("org.bstats", "${project.group}.wgrp.rslibs.lib.bstats")
        relocate("com.google.inject", "${project.group}.wgrp.rslibs.lib.inject")

        //Storage based relocations
        relocate("com.zaxxer.hikari", "${project.group}.wgrp.rslibs.lib.hikari")
        relocate("org.mariadb.jdbc", "${project.group}.wgrp.rslibs.lib.mariadb")
        exclude(":wgrp-api")
    }
    artifacts {
        archives(shadowJar)
    }
}

tasks {
    runServer {
        minecraftVersion("1.19.3")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "WorldGuardRegionProtect"
            groupId = rootProject.group as String?
            version = rootProject.version as String?

            from(components["java"])
        }
    }

    repositories {
        val mavenUrl = "https://repo.codemc.io/repository/maven-releases/"

        maven(mavenUrl) {
            val mavenUsername: String? by project
            val mavenPassword: String? by project
            if (mavenUsername != null && mavenPassword != null) {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}