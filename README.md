# Chronos – AI-Powered Reminder App [Download Here](https://drive.google.com/file/d/1vsH37gW02wLrI0iLv0ptlbrJsKVuUZYn/view?usp=sharing)
Chronos is a modern Android reminder application that allows users to create, manage, and schedule reminders. It also features an optional AI-powered greeting generator for sharing motivational or friendly messages. Built using the latest Android tools and best practices.

---
## Demo

https://github.com/user-attachments/assets/cfe1d27c-25e5-42c7-84ae-31871fbd1a36

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

## Screenshots 
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/89a8bea4-90bb-4c6a-9b38-5df085a5602b" />
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/6283b8e6-56c7-434d-973f-9495c406a857" />
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/d9078d58-bb9c-4670-8166-eeb3397c658e" />
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/dd174716-2708-498c-b215-f9da233c04dd" />
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/75f7ff92-72aa-4a66-b774-5781483f85e7" />
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/ff3620fc-17a8-4afc-a371-df7e5411736b" />
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/e7fbec17-d01a-48e0-adcc-ae7fce2e5f77" />
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/98da4455-efa7-478c-b02c-3458c6052507" />
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/09bc055c-3504-4f66-85c2-1a9eecc5f754" />
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/55278a53-e674-4d57-b9ff-ac1385a3b1f5" />
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/ae7312f9-3f56-4965-a092-5d2346612ca5" />
<img width="200" height="400" alt="image" src="https://github.com/user-attachments/assets/a6fe4323-2244-47f2-a6a5-7f5875a67395" />

- Firebase firestore
<img width="1436" height="705" alt="image" src="https://github.com/user-attachments/assets/8140a767-b9e4-4dc3-b7a3-6574d745c58d" />

- Firebase storage
<img width="1472" height="363" alt="image" src="https://github.com/user-attachments/assets/4a690d54-47f2-498a-9b5d-f655d7ce2f8d" />

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

