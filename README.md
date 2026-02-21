# Järnvägskrogen – Lunchmeny

An Android app that fetches and displays the weekly lunch menu from
[Järnvägskrogen](https://jarnvagskrogen.se), a restaurant at Gävle Central Station.

## Features

Fetches the current week's menu directly from jarnvagskrogen.se

## Requirements

- Android 8.0 (API 26) or higher
- Android Studio Hedgehog or newer (for Compose tooling)

## Building

Open the project in Android Studio and run the `app` configuration, or build from the command line:

```bash
./gradlew assembleDebug
```

## Architecture

```
MainActivity
└── LunchMenuScreen (Compose UI)
    └── LunchMenuViewModel
        └── MenuRepository
```

| Layer | Description |
|---|---|
| `MenuRepository` | Fetches HTML from jarnvagskrogen.se and parses it with Jsoup |
| `LunchMenuViewModel` | Holds `MenuUiState` as a `StateFlow`, auto-selects today's day on init |
| `LunchMenuScreen` | Renders the hero image, day chips, and menu cards |

## Tech stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Jsoup** for HTML scraping
- **Coil** for async image loading
- **Kotlin Coroutines** + **StateFlow** for async state management
