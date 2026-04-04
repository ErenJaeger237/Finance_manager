# Finance Tracker Fusion Redesign Tasks

- `[ ]`### Phase 1: Foundation (Completed)
- [x] Access Stitch MCP and read "Digital Vault" design tokens.
- [x] Update `Color.kt` with Dark/Light Glassmorphism palettes.
- [x] Update `Theme.kt` with token mappings.
- [x] Create `Modifiers.kt` for `glassmorphic` and `neomorphic` functions.

### Phase 2: UI Refactoring
- [x] Apply `glassmorphic` modifiers in `DashboardScreen.kt`.
- [x] Remove standard elevations and apply tonal changes- [x] **Refine Repository Logic**
  - [x] Update `insertTransactionWithGoalUpdate` in `FinanceRepository.kt` to handle both INCOME and EXPENSE for goals.
- [x] **Sync Goals with Transaction Ledger**
  - [x] Modify `addFunds` in `GoalsViewModel.kt` to create a traceable `EXPENSE` transaction instead of direct database updates.
- [x] **Improve Goal User Experience**
  - [x] Replace placeholder deadline with a Material 3 **DatePicker** in `GoalsScreen.kt`.
  - [x] Validate non-empty goal names and positive target amounts.
- [x] **Verification**
  - [x] Build the app and verify no regressions in budget/dashboard.
  - [x] Manually test "Add Funds" and check if it appears in transaction history.
  - [x] Test the new DatePicker functionality.
 Goals (Progress tracking, auto-track from income).
- [x] Fix Room migration and build errors (addMigrations, unresolved imports).

### Phase 3: Alignment & Memory
- [x] Update `Type.kt` based on the Editorial Authority hierarchy (tighter headers, relaxed body).
- [x] Update `BUILD_MEMORY.md` with timestamps.

### Phase 4: Expansion (Completed)
- [x] Move "Add Transaction" to FAB on Dashboard.
- [x] Implement Analytics V2 (Month comparison, daily trends line chart).
- [x] Implement Financial Goals (Progress tracking, auto-track from income).
- [x] Fix Room migration and build errors (addMigrations, unresolved imports).
