# Repository Guidelines

This project is a fork of [romainguy/kotlin-math](https://github.com/romainguy/kotlin-math).

## Project Structure & Module Organization

This single-module Kotlin Multiplatform library provides GLSL-inspired graphics math for JVM, JavaScript, Wasm, and native targets.

- `src/commonMain/kotlin/dev/romainguy/kotlin/math/` contains scalar, vector, matrix, quaternion, ray, half-precision, and rational APIs, grouped by topic.
- `src/commonTest/kotlin/dev/romainguy/kotlin/math/` contains shared tests.
- `build.gradle.kts` configures targets, dependencies, documentation, and publishing; `settings.gradle.kts` pins plugin versions.
- `README.md` documents public usage; `.github/workflows/` defines build and release automation. Generated artifacts belong in `build/`.

## Build, Test, and Development Commands

Use the checked-in Gradle wrapper from the repository root. The Gradle daemon and CI use JDK 21.

- `./gradlew build --continue` builds and checks the library, matching CI on macOS.
- `./gradlew jvmTest` runs shared tests on JVM for quick feedback.
- `./gradlew jvmTest --tests 'dev.romainguy.kotlin.math.VectorTest'` runs one test class.
- `./gradlew jsNodeTest` runs JavaScript tests in Node.js.
- `./gradlew dokkaGeneratePublicationHtml` generates HTML API documentation.

Native tests depend on host support and installed SDKs; browser tests require a browser. Report skipped or unavailable targets when sharing validation results. This library has no application entry point.

## Coding Style & Naming Conventions

Use four-space indentation and match surrounding Kotlin formatting. Use PascalCase for types, camelCase for functions and properties, and uppercase underscore-separated constants. Keep the existing `dev.romainguy.kotlin.math` package despite the Maven group being `io.technoirlab`.

Preserve the value-type API design and prefer GLSL-style top-level operations such as `normalize(v)`. Document public behavior with KDoc and update README examples when APIs change. No KtLint, Detekt, or formatter task is configured.

## Testing Guidelines

Use `kotlin.test` annotations and assertions in `commonTest`, with classes named `<Topic>Test`. Existing methods use camelCase and descriptive backtick names; follow the surrounding suite. Add regression tests for fixes, covering affected overloads, component boundaries, and relevant floating-point edge cases. No numerical coverage threshold is configured.

## Commit & Pull Request Guidelines

Use concise imperative commit subjects, following history: `Remove deprecated Apple targets` or `Restore Float3 comparisons and fix vector comparison bugs`. Do not add a `Co-Authored-By` trailer.

Keep pull requests focused. Describe the behavior change, link relevant issues, list validation commands and results, and identify API compatibility impacts. Ensure the build workflow passes before merging.
