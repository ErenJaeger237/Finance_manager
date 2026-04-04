# Finance Tracker Fusion Redesign Tasks

- `[ ]`### Phase 1: Foundation (Completed)
- [x] Access Stitch MCP and read "Digital Vault" design tokens.
- [x] Update `Color.kt` with Dark/Light Glassmorphism palettes.
- [x] Update `Theme.kt` with token mappings.
- [x] Create `Modifiers.kt` for `glassmorphic` and `neomorphic` functions.

### Phase 2: UI Refactoring
- [x] Apply `glassmorphic` modifiers in `DashboardScreen.kt`.
- [x] Remove standard elevations and apply tonal changes to `SmallSummaryCard`.
- [x] Update `TransactionListScreen.kt` (Remove Dividers, Add 16dp spacing).
- [x] Apply `surfaceContainerLow` and `surfaceContainerHighest` tonal layering using the "No-Line" rule.

### Phase 3: Alignment & Memory
- [x] Update `Type.kt` based on the Editorial Authority hierarchy (tighter headers, relaxed body).
- [x] Update `BUILD_MEMORY.md` with timestamps.

### Phase 4: Expansion (Completed)
- [x] Move "Add Transaction" to FAB on Dashboard.
- [x] Implement Analytics V2 (Month comparison, daily trends line chart).
- [x] Implement Financial Goals (Progress tracking, auto-track from income).
- [x] Fix Room migration and build errors (addMigrations, unresolved imports).
