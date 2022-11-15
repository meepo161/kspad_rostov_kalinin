plugins {
    kotlin("jvm") version "1.4.21"
    application
}

group = "ru.avem.stand"
version = "0.1"

repositories {
    mavenCentral()
    jcenter()

    flatDir {
        dirs("lib")
    }
}

application {
    mainClass.set("ru.avem.stand.MainKt")
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("no.tornado:tornadofx:1.7.18")
    implementation("no.tornado:tornadofx-controlsfx:0.1.1")
    implementation("de.jensd:fontawesomefx:8.9")
    implementation("de.jensd:fontawesomefx-materialicons:2.1-2")

    implementation("org.jetbrains.exposed:exposed:0.17.7")
    implementation("org.xerial:sqlite-jdbc:3.27.2.1")

    implementation("org.apache.poi:poi:4.1.0")
    implementation("org.apache.poi:poi-ooxml:4.1.0")

    implementation(":kserialpooler-1.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.jar {
    manifest {
        attributes["Class-Path"] = configurations.compile.map {
            it.name
        }

        attributes["Main-Class"] = application.mainClass
    }

    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}
