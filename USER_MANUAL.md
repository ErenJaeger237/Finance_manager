# 📱 Finance Manager — User Setup & Run Guide

Welcome to the **Finance Manager** project. This guide will help you set up your local environment and run the application on your machine or Android device.

---

## 🛠 Prerequisites

Ensure you have the following installed before proceeding:

1.  **Java Development Kit (JDK):** Version **21** is required.
2.  **Android SDK:** API Level **35** or higher.
3.  **Android Studio (Ladybug or newer):** Recommended for the best development experience.
4.  **Gradle:** Version **9.4.1** (Included in the project via the wrapper).

---

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone <your-repository-url>
cd Finance_manager
```

### 2. Configure Environment Variables
The project requires a `local.properties` file in the root directory to locate your Android SDK.

1. Create a file named `local.properties` in the root folder.
2. Add the following line (adjust the path to match your actual SDK location):

**Windows:**
```properties
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```

**macOS/Linux:**
```properties
sdk.dir=/Users/YourUsername/Library/Android/sdk
```

### 3. Build the Project
You can build the project using the command line or Android Studio.

**Using Command Line:**
```bash
# On Windows
.\gradlew build

# On macOS/Linux
./gradlew build
```

---

## 📱 Running the Application

### Option A: Using an Emulator (Recommended)
1. Open the project in **Android Studio**.
2. Go to **Device Manager** and start a Virtual Device (API 26+).
3. Click the **Run** button (Green Play icon) or press `Shift + F10`.

### Option B: Using a Physical Device
1. Enable **Developer Options** and **USB Debugging** on your Android phone.
2. Connect your phone via USB.
3. Run the following command to install the debug version:
   ```bash
   .\gradlew installDebug
   ```

---

## 📊 Project Structure Overview

-   **`app/src/main/java/.../data`**: Room Database, DAOs, and Repositories.
-   **`app/src/main/java/.../ui`**: Jetpack Compose screens, ViewModels, and Navigation.
-   **`app/src/main/java/.../util`**: Formatting utilities (Currency, Date).
-   **`ui/theme`**: Material3 design system and Olive Green palette.

---

## 💡 Key Features to Explore

1.  **Dashboard:** View your real-time balance and a Vico-powered bar chart showing expense breakdowns.
2.  **Transactions:** Add new income or expenses with custom categories.
3.  **Budgets:** Set monthly limits for specific categories and track progress via color-coded progress bars (Green → Orange → Red).

---

## ❓ Troubleshooting

### "SDK location not found"
Ensure your `local.properties` file exists and the `sdk.dir` path uses double backslashes `\\` on Windows.

### "Incompatible Java version"
This project uses **JDK 21**. Verify your version by running `java -version`. In Android Studio, go to `Settings > Build, Execution, Deployment > Build Tools > Gradle` and ensure the **Gradle JDK** is set to 21.

### "KSP version mismatch"
The project uses Kotlin 2.3.0 and KSP 2.3.0. If you change the Kotlin version, you must update the KSP version in `libs.versions.toml` to match.

---

## 📄 License
This project is developed as part of an academic portfolio. Feel free to use and modify for learning purposes.
