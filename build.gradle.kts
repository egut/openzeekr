import org.gradle.api.tasks.testing.logging.TestExceptionFormat

// Top-level build file. Plugin versions are declared here and applied per-module.
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("com.android.library") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24" apply false
}

// ---- Verification entry points --------------------------------------------------------------
// The single definition of "build, test, lint", used both locally and by CI, so passing
// locally means passing CI. Gradle is the cross-platform layer:
//   Linux/macOS:  ./gradlew verify
//   Windows:      gradlew.bat verify
// Individual steps: verifyBuild, verifyTest, verifyLint. Add --continue to see every
// failure in one run instead of stopping at the first.
//
// Lint policy (the lint {} block in each module):
//  - Lint ERRORS fail the build; warnings are reported but don't.
//  - Issues that already existed when the gate was introduced are grandfathered in each
//    module's lint-baseline.xml, so only NEW errors fail. After fixing a baselined issue,
//    or deliberately accepting a new one, regenerate with: ./gradlew updateLintBaseline
//  - GradleDependency ("a newer version is available") is disabled: it changes with every
//    upstream release, not with our code, so it would make the gate non-reproducible.
//  - lintDebug prints every issue (file:line, rule id) to the console; the HTML report is
//    under <module>/build/reports/.
val androidModules = listOf(":app", ":core", ":wear")

val verifyBuild by tasks.registering {
    group = "verification"
    description = "Assembles debug for all modules. Must pass on a clean clone with no secrets."
    dependsOn(androidModules.map { "$it:assembleDebug" })
}

val verifyTest by tasks.registering {
    group = "verification"
    description = "Runs the offline JVM unit tests (debug) for all modules."
    dependsOn(androidModules.map { "$it:testDebugUnitTest" })
}

val verifyLint by tasks.registering {
    group = "verification"
    description = "Runs Android Lint (debug) for all modules, gated by each module's baseline."
    dependsOn(androidModules.map { "$it:lintDebug" })
}

tasks.register("verify") {
    group = "verification"
    description = "Everything CI checks in Gradle: verifyBuild + verifyTest + verifyLint."
    dependsOn(verifyBuild, verifyTest, verifyLint)
}

// Show failing tests and their assertion/stack trace in the console, instead of only
// "There were failing tests" and a path to an HTML report.
subprojects {
    tasks.withType<Test>().configureEach {
        testLogging {
            events("failed", "skipped")
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }
}
