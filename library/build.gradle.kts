import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode.PRODUCTION
import org.jetbrains.kotlin.gradle.targets.js.ir.JsIrBinary
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmProject
import org.jetbrains.kotlin.gradle.targets.js.npm.npmProject

plugins {
    kotlin("multiplatform") version "1.4.255-SNAPSHOT"
    `maven-publish`
}
group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
    }
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlin-dev")
    }
}

kotlin {
    js {
        browser {

        }
        binaries.executable()

        val libraryDist by tasks.registering(Copy::class) {
            into(buildDir.resolve("dist"))
            from(
                    binaries
                            .matching { it.mode == PRODUCTION }
                            .map { it as JsIrBinary }
                            .map { it.linkTask }
            ) {
                into(NpmProject.DIST_FOLDER)
            }

            from(tasks.named(compilations["main"].npmProject.publicPackageJsonTaskName))
        }

        tasks.named("build") {
            dependsOn(libraryDist)
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(npm("date-arithmetic", "*"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}