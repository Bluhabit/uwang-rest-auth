/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
   // id("io.gitlab.arturbosch.detekt") version "1.22.0" apply false
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    kotlin("plugin.jpa") version "1.6.10"
}

apply {
  //  plugin("io.gitlab.arturbosch.detekt")
}


group = "com.bluehabit"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

val coroutinesVersion = "1.6.1"
repositories {
    mavenCentral()
}



dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    implementation("com.auth0:java-jwt:3.18.2")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.google.api-client:google-api-client:1.32.1")
    implementation("com.google.firebase:firebase-admin:9.0.0")

    implementation("org.postgresql:postgresql:42.3.1")
    runtimeOnly("mysql:mysql-connector-java")
    implementation("junit:junit:4.13.2")
    implementation("javax.activation:activation:1.1.1")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.3")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${coroutinesVersion}")

    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:2.3.0.RELEASE")

    implementation("org.assertj:assertj-core:3.21.0")
    //for application properties custom
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

//https://dev.to/akdevcraft/git-pre-hook-setup-pre-push-hook-for-gradle-project-example-1nn6
//https://emmanuelkehinde.io/setting-up-git-pre-commit-pre-push-hook-for-ktlint-check/
//tasks.create<Copy>("installGitHook") {
//    var suffix = "macos"
//    if (org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS)) {
//        suffix = "windows"
//    }
//
//    copy {
//        from(File(rootProject.rootDir, "scripts/pre-push-$suffix"))
//        into { File(rootProject.rootDir, ".git/hooks") }
//        rename("pre-push-$suffix", "pre-push")
//    }
//
//    copy {
//        from(File(rootProject.rootDir, "scripts/pre-commit-$suffix"))
//        into { File(rootProject.rootDir, ".git/hooks") }
//        rename("pre-commit-$suffix", "pre-commit")
//    }
//
//    //make file executable
//    fileMode = "775".toInt(8)
//}
//
//tasks.register("setup") {
//    dependsOn("installGitHook")
//}
//
//// Kotlin DSL
//tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
//    jvmTarget = "1.8"
//}
//tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
//    jvmTarget = "1.8"
//}