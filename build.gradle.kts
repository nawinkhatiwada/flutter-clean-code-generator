plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.23"
  id("org.jetbrains.intellij") version "1.17.2"
}

group = "com.androidbolts.fluttergenerator"
version = "1.0.6"

repositories {
  mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2024.3")
  type.set("IC")

  plugins.set(listOf(/* Plugin Dependencies */))
}



tasks {

  buildSearchableOptions {
    enabled = false
  }
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
  }

  patchPluginXml {
    sinceBuild.set("243")
    untilBuild.set("999.*")
  }

  runPluginVerifier {
    ideVersions.set(listOf(
      "2024.3",
      "2025.1",
      "2025.2"
    ))
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(project.findProperty("pluginToken").toString())
//    token.set(System.getenv("perm:bmF3aW4ua2hhdGl3YWRh.OTItMTAwNTY=.BY8voRHEzwPhuWvoTRmznhqSaXhb2i"))
  }
}
