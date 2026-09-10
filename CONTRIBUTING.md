# Contributing to GharKhata

We welcome contributions to GharKhata! Our goal is to build the most dignified, simple, and useful 100% offline household ledger for non-tech homemakers.

---

## Guiding Principles

1. **100% Offline by Default**: No cloud servers, no mandatory account sign-in, zero telemetry.
2. **Minimal Text & Visual First**: Non-tech users recognize icons (🥦, 🌾, 🥛, 🧹) and numbers 10x faster than English words.
3. **Ergonomic Simplicity**: Max 3 taps for data entry; large 56x56 dp touch targets; built-in calculator keypad.
4. **Data Longevity**: Never use destructive migrations in Room/SQLite.

---

## Development Workflow

1. Fork the repo and clone locally.
2. Ensure you have JDK 17 installed.
3. Verify test pass rate:
   ```bash
   ./gradlew test
   ```
4. Build debug APK:
   ```bash
   ./gradlew assembleDebug
   ```
5. Submit a pull request with clear before/after notes.
