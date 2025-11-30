plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "kj455"
version = "0.0.1"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        create("IC", "2025.1.4.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        // Add necessary plugin dependencies for compilation here, example:
        // bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
            <h3>0.0.1</h3>
            <ul>
                <li>Initial release</li>
                <li>Select inside parentheses () - cmd+k, (</li>
                <li>Select inside square brackets [] - cmd+k, [</li>
                <li>Select inside curly brackets {} - cmd+k, {</li>
                <li>Select inside single quotes '' - cmd+k, '</li>
                <li>Select inside double quotes "" - cmd+k, "</li>
                <li>Select inside backticks `` - cmd+k, `</li>
                <li>Auto-expand to include surrounding brackets/quotes on second press</li>
                <li>Support for nested brackets</li>
                <li>Multiline support for backticks</li>
            </ul>
        """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
