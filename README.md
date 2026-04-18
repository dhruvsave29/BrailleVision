# BrailleVision

Offline braille-to-text recognition with on-device YOLOv8 ML model and text-to-speech.

- **Android**: API 24+ (Android 7.0+)
- **Model**: 96.76% mAP@50
- **ML**: YOLOv8-nano (TFLite)

[Features](#features) - [Technical Details](#technical-details) - [Building](#building)

BrailleVision is a completely offline Android application that captures braille text images using the device camera, converts them to text using an on-device YOLOv8-nano ML model (TensorFlow Lite), and speaks the recognized text aloud using Android's text-to-speech engine.

## Features

- **Offline Braille Recognition** - On-device ML model, no internet connection required
- **Camera Integration** - Capture braille text using CameraX with flash and gallery support
- **Text-to-Speech** - Speak recognized text aloud with adjustable rate and pitch
- **History** - Save and review past recognitions with Room database
- **Settings** - Customize behavior with DataStore preferences:
  - Auto-speak after recognition
  - Camera guide overlay
  - Auto-save to history
  - Spell correction

## Technical Details

- **ML Model**: YOLOv8-nano (Ultralytics)
- **Input Size**: 320x320 pixels
- **Output Shape**: [1, 30, 2100] (transposed format: features, boxes)
- **Classes**: 26 (a-z English letters)
- **Accuracy**: 96.76% mAP@50 on test set
- **Inference**: CPU with 4 threads (no GPU delegate for compatibility)
- **TFLite**: Full integer quantization, no GPU delegate
- **Target**: Android API 24+ (Android 7.0 Nougat)

### Architecture

```
UI Layer (Jetpack Compose + Material3)
Domain Layer (Use Cases: SpeakText, SaveHistory)
Data Layer (TFLite YoloDetector, Room HistoryDB, DataStore Preferences)
```

## Getting Started

### Prerequisites

- Android Studio Jellyfish or later
- Android SDK 35
- Gradle 8.9+
- Java 17

### Project Structure

```
BrailleVisionV2/
├── app/src/main/
│   ├── java/com/braillevision/v2/
│   │   ├── data/           # TFLite, Room, DataStore
│   │   ├── di/             # Hilt dependency injection
│   │   ├── domain/         # Use cases and models
│   │   ├── ui/              # Compose screens
│   │   └── navigation/      # Navigation graph
│   └── assets/
│       ├── best_float32.tflite  # ML model (11.6 MB)
│       └── labels.txt            # Class labels
├── build.gradle.kts
└── settings.gradle.kts
```

## Building

```bash
git clone https://github.com/dhruvsave29/BrailleVision.git
cd BrailleVision
./gradlew.bat assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Usage

1. **Capture** - Point camera at braille text and tap capture
2. **Recognize** - Image processed locally with TFLite
3. **Speak** - Text spoken aloud (if auto-speak enabled)
4. **History** - Past recognitions saved in History screen

### Settings

- **Speech Rate** - TTS speed (0.5x - 2.0x)
- **Pitch** - TTS pitch (0.5 - 2.0)
- **Auto-speak** - Auto read after recognition
- **Guide Overlay** - Camera alignment guide
- **Auto-save** - Save to history automatically
- **Spell Correction** - SymSpell algorithm

## Tech Stack

- **Language**: Kotlin 2.0
- **UI**: Jetpack Compose + Material3
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **ML**: TensorFlow Lite
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35
- **Database**: Room
- **Camera**: CameraX
- **Preferences**: DataStore

---

Built with accessibility in mind. BrailleVision helps visually impaired users read braille text through camera capture and text-to-speech.