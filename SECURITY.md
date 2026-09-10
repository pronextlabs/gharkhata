# Security Policy

## Privacy-First Architecture

GharKhata is engineered with a **zero-trust local-first security model**:
* **Zero Network Permissions**: The app manifest requests no internet access (`android.permission.INTERNET` is omitted). Your financial and household data physically cannot leave your phone.
* **Gupt Tijori Protection**: Private emergency savings and confidential vaults are protected behind biometric authentication (`BiometricPrompt` on Android, `LocalAuthentication` on iOS).
* **Local Sandboxing**: SQLite databases reside exclusively in the protected application internal storage sandbox.

## Reporting Vulnerabilities

If you discover a security issue or local data exposure vulnerability, please email `security@pronextlabs.com` or open a confidential security advisory on GitHub.
