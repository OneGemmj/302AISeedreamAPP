# Seedream 302 Android

Native Android app for 302.AI Seedream image generation and editing, implemented with Kotlin, Jetpack Compose, Room, Android Keystore, and a foreground generation service.

## Features

- Seedream 5.0 / 4.5 model selection.
- API key save, show/hide, clear, encrypted with Android Keystore.
- Editable API endpoint and latency test.
- Prompt input, multiple local reference images, URL reference images, sorting and deletion.
- Payload options for size, seed, response format, watermark, streaming, sequential image generation, max images, and 5.0 web search.
- Foreground service while generating, notification stop action, retry for 5xx/network failures.
- JSON and SSE response parsing.
- Result preview, fullscreen view, save to `Pictures/Seedream`.
- Local history with Room and app-private image cache.
- Bottom-tab UI for Create, Result, History, and Debug views.

## Build

Required tools:

- JDK 17
- Android SDK with API 37 and Build Tools 37.0.0
- Android Studio or the bundled Gradle wrapper

Create a local `local.properties` file if Android Studio did not create it:

```properties
sdk.dir=C\:\\Android\\Sdk
```

Then build:

```powershell
.\env.ps1
.\gradlew.bat test assembleDebug
```

Debug APK output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

`local.properties`, generated APKs, signing keys, and local environment files are intentionally ignored by Git.

## Versioning

The app version is controlled in `gradle.properties`:

```properties
APP_VERSION_CODE=2
APP_VERSION_NAME=1.1.0
```

Before each release, increment both values. `versionCode` must always increase; `versionName` should match the Git tag, for example `v1.1.0`.

See [docs/RELEASE.md](docs/RELEASE.md) for the release workflow.
