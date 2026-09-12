# Changelog

All notable changes to **GharKhata** will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.1.0] - 2026-09-12

### Security & Privacy Hardening
- **Biometric Security Vault**: Integrated native AndroidX `BiometricPrompt` and device PIN fallback in `SavingsScreen` for Gupt Tijori protection.
- **Local Data Extraction Hardening**: Set `android:allowBackup="false"` in `AndroidManifest.xml` to prevent raw ADB extraction of sensitive financial data.
- **R8 / ProGuard Rules**: Added `app/proguard-rules.pro` with explicit keep rules for Room SQLite, Coroutines, and Biometric prompt modules.

### Fixed & Enhanced
- **Modern SDK Upgrade (Android 15)**: Upgraded `targetSdk` and `compileSdk` to 35 (Android 15) to comply with Google Play requirements and resolve outdated OS warnings.
- **Android Gradle Plugin (AGP 8.6.1)**: Upgraded AGP from 8.4.2 to 8.6.1 for native Android 15 toolchain support.
- **Edge-to-Edge & Predictive Back**: Implemented `enableEdgeToEdge()` in `MainActivity` (extending `AppCompatActivity`) and enabled `android:enableOnBackInvokedCallback="true"`.
- **Monthly Cumulative Spending Calculation**: Fixed `HomeScreen` pacer logic to observe `getMonthlySpendSum(firstEpochDay, todayEpoch)` so dynamic daily safe spend accounts for the full month's cumulative expenses instead of only today's expenses.
- **Persistent Local State**: Introduced `GharKhataPreferences` to save and restore monthly budgets, Gupt Tijori savings, and Galla physical cash note counts across sessions.
- **Room Database Wiring for Services**: Connected `ServicesScreen` to `GharKhataDao`, persisting milk logs and domestic helper staff attendance/advances directly to Room SQLite.
- **iOS Parity & Version Sync**: Aligned iOS version string in `ContentView.swift` to 1.1.0 and updated release archive.
- **Primary Enter Button**: Added a dedicated full-width "Kharcha Jodein (Add Expense)" action button directly below the Mandi keypad.
- **Screen Scrolling & Overflow**: Replaced unconstrained category grid with deterministic 2-row layout and enabled vertical scrolling for all screen sizes.
- **Payment Mode Toggle**: Added Cash vs Online UPI 1-tap pill selector.
- **Mandi Math Evaluation**: Supported multi-addition and subtraction expressions (e.g. `40+60+35`).
- **Today's Expense Stream**: Displayed recorded transactions for the current day with delete/undo support.
- **Visual Branding**: Integrated high-resolution application logo and adaptive launcher icons.
- **Attribution**: Added ProNext Labs engineering credits in Settings screen.

## [1.0.0] - 2026-09-10

### Added
- **100% Offline-First Architecture**: Android Jetpack Compose + Material 3 + Room SQLite database.
- **Aaj Ka Kharcha (Daily Safe Spend Pacer)**: Dynamic formula calculating safe spend based on days remaining in the month.
- **Mandi Calculator Keypad**: Thumb-zone 0ms response keypad with built-in `+`, `-` operators and Emil Kowalski tactile haptics.
- **Doodh Ka Hisaab (Milk Bottle Tally)**: Visual calendar grid of milk bottles (1.0L, 1.5L, 2.0L, Bandh) with auto-updating monthly total bill and 1-tap WhatsApp summary slip.
- **Kamwali / Bai (Domestic Staff Payroll)**: Visual attendance calendar, advance deduction tracking, and auto-calculated net salary on the 1st of the month.
- **Gupt Tijori (Secret Savings Vault)**: Biometric/PIN-isolated stash for personal savings (*Stree Dhan*).
- **Galla (Cash Denomination Counter)**: Note steppers for ₹500, ₹200, ₹100, and ₹50 with instant multiplication.
- **Indian Rupee (₹) Formatting**: Native Lakhs and Crores numbering conventions (`₹ 1,45,000`).
- **Zero Internet Permissions**: Complete privacy guarantee by omitting internet access from the manifest.
