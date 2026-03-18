# Scoop (Android App)

This is the Android client for the Transcribe Assistant service. It lets you request transcripts for video URLs, view and cache past transcripts, and browse results—all in a clean, modular architecture.

## 🚀 To get started

1. **Set up and run the [backend service] (https://github.com/akshataxx/content-categorise))**
   Follow that repo’s README to launch on `localhost:8080`. The backend needs:

   * An OpenAI API key (in `application-secrets.properties`)

   * `yt-dlp`

   * `ffmpeg`

   > **Tip:** In the emulator, use `http://10.0.2.2:8080/` to reach your host’s localhost.

2. **Build and run this Android app**

   * Open in Android Studio
   * Let Hilt generate DI components
   * Run on an emulator or device

---

## 🔧 Build Variants

The app has different configurations for local development and production:

| Build Type | API URL | Protocol |
|------------|---------|----------|
| `debug` | `http://10.0.2.2:8081` | HTTP (local backend) |
| `release` | `https://34-151-189-90.sslip.io` | HTTPS (production) |

### Switching Build Variants in Android Studio

1. Go to **View** → **Tool Windows** → **Build Variants**
2. A panel appears on the left side
3. Click the dropdown next to `:app`
4. Select `debug` or `release`
5. Run the app as normal

> **Note:** Release builds require signing config in `keystore.properties`

---

## 🧱 Project Structure Overview

This project follows **Clean Architecture** principles—UI, business logic, and data are strictly separated to improve maintainability and testability.

| Package         | Description                                                                                                                     |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------- |
| `ui`            | Presentation layer using Jetpack Compose. Contains Composables, ViewModels, state holders, and navigation graph.                |
| `domain`        | Core business rules & models. Plain Kotlin data classes, repository interfaces, use-case classes—no Android or framework deps.  |
| `domain/mapper` | Extension functions that convert between layers (DTO ↔ domain ↔ entity), preventing cross-layer leaks.                          |
| `data`          | Data layer implementations: network (Retrofit DTOs & API interfaces), local cache (Room entities & DAOs), and repository logic. |
| `di`            | Hilt modules: binds Retrofit, Room, repositories, and ViewModels for dependency injection throughout the app.                   |

---

### 🎨 `ui` (Presentation)

* **Screens** (`ui/screen`): Jetpack Compose Composables for each feature screen (e.g. `TranscriptListScreen`, `TranscriptDetailScreen`).
* **ViewModels** (`ui/viewmodel`): Expose UI state and handle user events. They call domain use-cases or repos and survive config changes.
* **Theme** (`ui/theme`): Color palettes, typography, shapes to ensure a consistent design system.

---

### 🧠 `domain` (Business)

* **Models** (`domain/model`): Core data classes (e.g. `Transcript`) representing your app’s concepts.
* **Repository Interfaces** (`domain/repository`): Contracts like `TranscriptRepository`—the app depends on these, not their implementations.
* **Use-Cases / Interactors** (`domain/usecase`): Encapsulate single operations (e.g. `GetTranscriptUseCase`) for clear, testable business logic.

---

### 🔄 `domain/mapper`

* **DTO → Domain**: `TranscriptDto.toDomain()`
* **Domain → Entity**: `Transcript.toEntity()`
* **Entity → Domain**: `TranscriptEntity.toDomain()`

Mapping functions live here so each layer only sees the data shape it needs.

---

### 💾 `data` (Data)

* **Network** (`data/network`):

  * `TranscriptApi.kt` (Retrofit interface)
  * DTO classes in `data/dto/` matching JSON structures

* **Cache** (`data/cache`):

  * `AppDatabase.kt` (Room database)
  * DAOs in `data/cache/dao/` (e.g. `TranscriptDao`)
  * Entities in `data/cache/entity/`

* **Repository Impl** (`data/repository`):
  Implements `domain` interfaces, orchestrating API calls, caching, and mapping logic (e.g. `TranscriptRepositoryImpl`).

---

### 💉 `di` (Dependency Injection)

* **Hilt Modules** (`di`):

  * `TranscriptModule.kt`: Provides singletons for Retrofit, Room, DAOs, and repo implementations.
  * Binds ViewModels with `@HiltViewModel`
