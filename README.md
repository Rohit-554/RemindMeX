# Chronos – AI-Powered Reminder App

Chronos is a modern Android reminder application that allows users to create, manage, and schedule reminders. It also features an optional AI-powered greeting generator for sharing motivational or friendly messages. Built using the latest Android tools and best practices.

---

## Features

### Reminder Management

* Add, edit, and delete reminders
* Each reminder includes a title, date and time, optional notes, and an optional image
* Images can be selected from the gallery or camera
* Uploaded images are stored in Firebase Storage
* Reminders are stored in Firebase Firestore

### Notifications

* Reminders trigger local notifications at the scheduled time
* Uses AlarmManager for precise scheduling
* Handles notification permissions as per Android 13+

### Authentication

* Google Sign-In using Firebase Authentication
* Secure login and logout

### AI Greeting Generator

* Generates dynamic greeting messages using the Gemini API
* Messages can be motivational, friendly, or custom
* Allows users to share messages using Android’s share sheet

### UI/UX

* Built using Jetpack Compose with Material 3
* Responsive and clean design
* Supports dynamic light/dark themes with manual toggle

---

## Tech Stack

* **Language:** Kotlin
* **UI Framework:** Jetpack Compose
* **Architecture:** MVVM with Clean Architecture
* **Dependency Injection:** Koin
* **Database:** Firebase Firestore
* **Storage:** Firebase Storage
* **Authentication:** Firebase Auth with Google Sign-In
* **Notifications:** AlarmManager
* **AI Integration:** Gemini API

---

## Modules

* **data:** Handles Firebase access, image uploading, and external APIs
* **domain:** Contains business logic, models, and use cases
* **presentation:** UI built with Jetpack Compose and ViewModels

---

## Setup Instructions

1. Clone this repository.
2. Open the project in Android Studio.
3. Add your Firebase configuration files (`google-services.json`).
4. Replace the Gemini API key in the `GeminiApiClient` file.
5. Sync the project and run on a device or emulator.

---

## Project Structure

```
- data
  - repository
  - firebase
  - api
- domain
  - model
  - usecase
- presentation
  - ui
  - viewmodel
- di
  - AppModule.kt
- services
```

---

## References
1. [Pages](https://github.com/Rohit-554/Pages)

Made with 🤩 By Jadu 

