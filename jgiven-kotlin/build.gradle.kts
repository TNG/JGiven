import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.21"
  id("org.jetbrains.kotlin.plugin.allopen") version "1.3.21"
}

dependencies {
  api(project(":jgiven-core"))

  implementation(kotlin("stdlib-jdk8"))

  testImplementation(project(":jgiven-junit"))
}

allOpen {
  annotation("com.tngtech.jgiven.kotlin.JGivenStage")
}


val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
  jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
  jvmTarget = "1.8"
}