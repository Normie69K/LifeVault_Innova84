# 📱 LifeVault Mobile dApp (Android)

Welcome to the native Android client for the **LifeVault Protocol**. 

This application allows "Explorers" to physically interact with the real world to capture, encrypt, and permanently store their memories on the Aptos blockchain. Built entirely with modern Android development standards, it seamlessly bridges native mobile capabilities (like Camera and Biometrics) with Web3 decentralized infrastructure.

## 🛠️ Tech Stack & Architecture

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (100% declarative UI)
* **Architecture:** MVVM (Model-View-ViewModel)
* **Networking:** Retrofit2 & OkHttp
* **Web3 Integration:** Custom Aptos SDK wrappers (`AptosClient.kt`, `Web3Client.kt`)
* **Cryptography:** Native `CryptoManager.kt` for local Ed25519 keypair generation and signing
* **Decentralized Storage:** Direct IPFS pinning via `IPFSClient.kt`

---

## 📂 Project Structure

The codebase is organized by feature and layer to ensure clean separation of concerns:

```text
lifeVault-dApp/
├── app/src/main/java/com/codebyte/lifevault_dapp/
│   ├── core/               # 🧠 Web3, IPFS, and Cryptography engines
│   ├── data/               # 🌐 API services, Repositories, and Data Models
│   ├── ui/
│   │   ├── components/     # Reusable Compose UI (MemoryCard, QRScanner, Modals)
│   │   ├── navigation/     # App routing logic
│   │   ├── screens/        # Full-page View components (Wallet, Timeline, Map)
│   │   └── theme/          # Material Design color palettes and typography
│   ├── MainViewModel.kt    # Global state management
│   └── MainActivity.kt     # Single-Activity entry point

```

---

## 🚀 Local Setup Guide

Follow these steps to run the LifeVault dApp on your local machine or physical Android device.

### 1. Prerequisites

* Download and install the latest version of [Android Studio](https://developer.android.com/studio).
* Ensure you have the Android SDK (API 34+) installed.
* A running instance of the **LifeVault Backend API**.

### 2. Open the Project

1. Open Android Studio.
2. Select **File > Open** and navigate to the `lifeVault-dApp` directory.
3. Allow Gradle a few minutes to sync and download all dependencies defined in `build.gradle.kts`.

### 3. Network Configuration (Crucial Step)

Mobile emulators and physical devices *cannot* use `http://localhost` to talk to your backend computer. You must point the app to your computer's local IP address.

1. Find your computer's local IP address (e.g., `192.168.1.45`).
* *Mac/Linux:* Run `ifconfig` or `ip a` in terminal.
* *Windows:* Run `ipconfig` in command prompt.


2. Open `app/src/main/java/com/codebyte/lifevault_dapp/data/NetworkModule.kt` (or wherever your Retrofit Base URL is defined).
3. Update the Base URL to point to your backend:
```kotlin
// Change this:
// const val BASE_URL = "http://localhost:5000/api/"

// To your Local IP:
const val BASE_URL = "[http://192.168.1.45:5000/api/](http://192.168.1.45:5000/api/)"

```



### 4. Configure Web3 & IPFS Keys

Open `AptosConfig.kt` or `IPFSClient.kt` inside the `core/` directory and ensure your environment variables (like Pinata API keys or Aptos Devnet/Testnet Node URLs) are properly set for your local environment.

### 5. Build and Run

1. Connect a physical Android device via USB (with USB Debugging enabled in Developer Options) OR start an Android Virtual Device (Emulator) from the Device Manager.
2. Click the green **Run (Play)** button in the top toolbar of Android Studio.
3. The APK will compile and install onto your device!

---

## 🔑 Key Features

* **Non-Custodial Wallet Generation (`CryptoManager.kt`):** Generates and securely stores an Aptos-compatible Ed25519 Keypair directly within the Android Keystore system. Private keys never leave the user's device.
* **Direct IPFS Uploads (`IPFSClient.kt`):** Photos taken via the app are hashed and pinned directly to the decentralized web before the metadata is ever sent to the blockchain.
* **Proof-of-Experience Verification:** Utilizes device hardware (Camera, GPS) to feed verifiable data to the LifeVault AI backend, unlocking physical quests and NFT badges.

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! If you plan to modify the UI, please adhere to the existing Jetpack Compose paradigms found in the `ui/components/` directory.
