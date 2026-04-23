# BrailleVision

> Offline braille-to-text recognition for Android, powered by an on-device YOLOv8-nano TFLite model and spoken back with Text-to-Speech.

BrailleVision is an accessibility-first Android app that captures braille images, runs detection fully on-device, decodes the result into text, optionally spell-corrects it, and speaks it aloud. Everything works offline for privacy, field reliability, and low-latency use.

## HTML Showcase

The animated HTML version of this project lives at [`index.html`](/C:/Users/dhruv/AndroidStudioProjects/BrailleVisionV2/index.html). Once GitHub Pages is enabled, it can also be served as the live site.

## Highlights

- Fully offline ML pipeline, from camera capture to TTS
- YOLOv8-nano TFLite model with 320x320 RGB input and `[1, 30, 2100]` output
- 26-class alphabet support (`a` to `z`)
- CameraX capture with EXIF-aware rotation handling
- Compose-based navigation and UI
- Room history persistence and DataStore-backed settings
- Neo-brutalist visual language with sharp edges, thick borders, and yellow accents

## How It Works

1. The app captures a braille image with CameraX or loads one from the gallery.
2. The bitmap is rotated and preprocessed for the model input size.
3. `YoloDetector` runs on CPU using 4 threads and parses the transposed model output.
4. Detected braille is converted to text, then optionally spell-corrected with `SymSpell`.
5. The final text can be saved to history and spoken aloud with TTS.

> [!NOTE]
> No cloud services are used. Recognition, decoding, settings, history, and speech all work on-device.

## Features

- Camera capture with flash and gallery support
- On-device braille detection using `best_float32.tflite`
- Real detection results only, no demo or placeholder data
- Spell correction toggle for post-processing
- TTS playback with adjustable rate and pitch
- History screen powered by Room
- Persistent settings with DataStore

## Model Details

- **Model**: YOLOv8-nano TFLite
- **Input**: `320 x 320 x 3` RGB
- **Output**: `[1, 30, 2100]`
- **Classes**: 26 letters
- **Runtime**: CPU only
- **Threads**: 4
- **Format**: float32 `.tflite`

The model and label file live in:

- `app/src/main/assets/best_float32.tflite`
- `app/src/main/assets/labels.txt`

`labels.txt` should contain exactly 26 lines, one per class.

## Project Structure

```text
app/src/main/java/com/braillevision/v2/
|-- BrailleVisionApp.kt
|-- navigation/NavGraph.kt
|-- data/
|   |-- local/HistoryDao.kt, HistoryEntity.kt
|   |-- preferences/PreferencesManager.kt
|   |-- spell/SymSpell.kt
|   `-- tflite/YoloDetector.kt
|-- domain/
|   |-- model/RecognitionResult.kt
|   `-- usecase/SaveHistoryUseCase.kt
|-- di/
|   |-- AppModule.kt
|   |-- DatabaseModule.kt
|   `-- TFLiteModule.kt
`-- ui/
    |-- camera/
    |-- result/
    |-- history/
    `-- settings/
```

## Settings

Settings are persisted with DataStore and reflected immediately in the UI.

| Setting | Default | Purpose |
| --- | --- | --- |
| `autoSpeak` | `false` | Speak results automatically after recognition |
| `speechRate` | `1.0` | Control TTS speaking speed |
| `pitch` | `1.0` | Control TTS pitch |
| `showGuide` | `true` | Display the camera alignment guide |
| `autoSaveHistory` | `false` | Save detections to history automatically |
| `spellCorrection` | `true` | Enable SymSpell post-processing |

## Getting Started

### Prerequisites

- Android Studio
- Android SDK with a compatible build setup
- A device or emulator for testing

### Build

```bash
./gradlew assembleDebug
```

On Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

### Install

Install the generated debug APK from:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Notes

- The app is designed to work entirely offline.
- Avoid changing model input, EXIF rotation, or NMS logic without validating with real images.
- The repository is structured to keep settings in DataStore and history in Room, not in transient UI state.
