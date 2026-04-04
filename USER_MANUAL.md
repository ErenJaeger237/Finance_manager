# Finance Manager - User Manual

## Prerequisites
- JDK 21
- Android SDK API 36+
- Android Studio (Ladybug or newer)
- Gradle 9.4.1 (included via wrapper)

## Getting Started

### 1. Clone and Configure
`
git clone <your-repository-url>
cd Finance_manager
`
Create local.properties in root:
`
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
`

### 2. Build
`
gradle assembleDebug
`

### 3. Run
**Emulator:** Open in Android Studio > Device Manager > Run (Shift+F10)
**Physical:** Enable USB Debugging > connect > gradle installDebug

## App Features

### Dashboard
- Real-time balance overview with glassmorphic frosted-glass background
- Inflow/Outflow summary cards with neomorphic depth effect
- Vico bar chart with labeled Y-axis (amounts) and X-axis (categories)
- Top spending categories breakdown
- Manual Dark/Light mode toggle in top bar

### Transactions
- Add income or expenses with custom categories
- Clean tonal surface layering (No-Line design rule)
- Full transaction history with category icons

### Budgets
- Set monthly spending limits per category
- Frosted-glass budget cards (glassmorphic)
- Color-coded progress bars: Green (safe) > Orange (warning at 80%) > Red text (exceeded)
- Compact floating action button to add new budgets

## Design System: Digital Vault
The app uses a custom  Digital Vault theme combining:
- **Glassmorphism:** Frosted translucent surfaces with subtle borders
- **Neomorphism:** Soft shadow depth on interactive cards
- **No-Line Rule:** No dividers; hierarchy via tonal surface shifts
- **Editorial Typography:** Weighted heading hierarchy

## Project Structure
- data/ - Room Database, DAOs, Repositories
- ui/dashboard/ - Dashboard screen and ViewModel
- ui/transaction/ - Transaction list and add screens
- ui/budget/ - Budget tracking screen
- ui/theme/ - Colors, Type, Modifiers (glassmorphic/neomorphic)
- util/ - Currency and Date formatters

## Troubleshooting

| Problem | Solution |
|---|---|
| SDK location not found | Check local.properties path with double backslashes |
| Incompatible Java version | Verify JDK 21 via java -version |
| KSP version mismatch | Match KSP version to Kotlin in libs.versions.toml |
| Chart axes missing | Need all 4 Vico imports (see BUILD_MEMORY_V2.md) |

## License
Academic portfolio project. Free to use and modify for learning.
