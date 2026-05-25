# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Distant Worlds - Muzei is an Android wallpaper provider for the [Muzei](https://muzei.co/) live wallpaper app. It fetches
images from Imgur albums containing screenshots from the Elite Dangerous "Distant Worlds" expeditions and serves them as
rotating wallpapers via Muzei's art provider API. It supports two sources: Distant Worlds 1 (3302) and Distant Worlds 2 (
3305).

## Build & Development Commands

```bash
# Build
./gradlew assembleDebug

# Run unit tests (JUnit 5)
./gradlew testDebugUnitTest

# Lint
./gradlew lintDebug
```

Tests use JUnit 5 (Jupiter) — the build is configured with `useJUnitPlatform()`.

## API Keys

Imgur credentials live in `apikeys.properties` at the project root (git-ignored). The build auto-creates a placeholder file
if missing. Required keys: `imgur_client_id`, `imgur_dw_album_id`, `imgur_dw2_album_id`.

## Architecture

Single-module app (`app/`) using Kotlin, Jetpack Compose (Material 3), and Hilt for DI.

### MVI pattern in the About screen

`AboutView.kt` defines the contract using sealed classes/interfaces:

- `State` — UI state (`Idle`, `InstallMuzeiPrompt`, `SelectDWSource`)
- `Navigation` — one-shot effects (navigation events)
- `UIAction` — user interactions

`AboutViewModel` exposes `state: MutableStateFlow<State>` and `effect: MutableSharedFlow<Navigation>`. The Activity collects
both and delegates rendering to `AboutScreen` (Compose).

### Muzei integration

Two `MuzeiArtProvider` subclasses (`DistantWorldsArtProvider`, `DistantWorlds2ArtProvider`) each trigger
`ImgurWorker.enqueueLoad(source)` on load requests. The worker fetches the album from Imgur's API via Retrofit, maps images
to `Artwork` objects, shuffles them, and posts them to Muzei's `ProviderContract`.

### Key data flow

`MuzeiArtProvider.onLoadRequested` → `ImgurWorker` (WorkManager) → Imgur REST API (Retrofit + OkHttp) →
`ProviderContract.addArtwork`

## Testing Conventions

- JUnit 5 with `@BeforeEach` setup
- Turbine for Flow assertions (`flow.test { awaitItem() }`)
- MockK for mocking
- kotlinx-coroutines-test for coroutine testing
- Test names use backtick descriptive format: `` `given X, when Y, then Z` ``

## Build Configuration Notes

- Kotlin warnings are treated as errors (`allWarningsAsErrors`, `warningsAsErrors`)
- Java toolchain targets JDK 21; Kotlin/Android compile to JVM 11
- R8 minification and resource shrinking are enabled for release builds
- Version catalog at `gradle/libs.versions.toml` manages all dependency versions
