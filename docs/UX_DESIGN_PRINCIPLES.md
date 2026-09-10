# UX Design Principles for Non-Tech Homemakers

This guide outlines design rules to ensure any homemaker—regardless of digital literacy or language comfort—can use the app effortlessly.

---

## 1. Ergonomic Rules

| Design Parameter | Standard App Default | GharKhata Standard | Why It Matters |
| :--- | :--- | :--- | :--- |
| **Touch Target Size** | 40 × 40 dp | **56 × 56 dp minimum** | Prevents accidental mis-taps on small phone screens while multitasking in the kitchen. |
| **Primary Number Size** | 16–20 sp | **28–34 sp (Bold)** | Instantly readable at arm's length without squinting or putting on reading glasses. |
| **Action Steps** | 5–7 taps + typing | **Max 3 taps, zero typing** | Keeps the friction lower than pulling out a pen and paper diary. |
| **Keyboard Type** | Full QWERTY keyboard | **Large Number Pad with `+` / `-`** | QWERTY typing is stressful and slow for non-English/non-tech users. |
| **Feedback** | Visual only | **Tactile Haptic + Soft Audio Chime** | Physical sensation confirms that the expense was recorded successfully. |

---

## 2. Visual Recognition Hierarchy (No English Reading Required)

Non-tech users identify **shapes, colors, and objects** 10x faster than reading English text. Every category must use an unmistakable, life-like visual symbol:

```
[ 🌾 ] Ration / Kirana     [ 🥦 ] Sabzi & Phal        [ 🥛 ] Doodh / Dairy
[ 🧹 ] Kamwali / Bai       [ ⛽ ] Gas Cylinder        [ 📚 ] Baccho Ki Padhai
[ 💊 ] Dawai / Doctor      [ 🪔 ] Pooja / Shagun      [ 👗 ] Kapde / Tailor
```

---

## 3. The 3-Tap Flow (Screen Walkthrough)

```
+-----------------------------------+
|  [ 🥦 Sabzi ]   [ 🥛 Doodh ]      |  <-- STEP 1: Tap the Category Icon
|  [ 🌾 Ration ]  [ 🧹 Kamwali ]    |
+-----------------------------------+
|               ₹ 120               |  <-- Display shows calculated amount
+-----------------------------------+
|   [ 1 ]       [ 2 ]       [ 3 ]   |  <-- STEP 2: Tap the Numbers
|   [ 4 ]       [ 5 ]       [ 6 ]   |
|   [ 7 ]       [ 8 ]       [ 9 ]   |
|   [ + ]       [ 0 ]       [ ⌫ ]   |
+-----------------------------------+
|         [ ✓ HO GAYA ]             |  <-- STEP 3: Tap Big Green Button
+-----------------------------------+
```

* **Zero Dropdowns**: Never ask the user to pick an account, date, time, or sub-category.
* **Auto-Defaults**:
  * Date = Today.
  * Payment mode = Cash (1-tap pill to toggle to UPI/Online).
  * Time = Current time.

---

## 4. Voice Input ("Bolke Likhein")

For users who prefer speaking over tapping:
* Prominent, friendly microphone button in the top bar.
* User taps and speaks naturally:
  * *"Sabzi 140 rupaye"* $\rightarrow$ Auto-selects 🥦 Sabzi, enters ₹140.
  * *"Doodh 60"* $\rightarrow$ Auto-selects 🥛 Doodh, enters ₹60.
  * *"Sunita ko advance 500"* $\rightarrow$ Auto-records ₹500 advance for maid.
* Speaks back a brief, pleasant voice confirmation in the selected language:
  > *"₹140 Sabzi jod diya gaya hai."*

---

## 5. Forgiving, Fear-Free Experience

Non-tech users often fear *"breaking the app"* or losing their data:
1. **The 5-Second Undo Pillow**:
   * Whenever an item is logged or changed, a friendly snackbar appears:
     > *"₹120 Jod Diya! [ Galti Se Hua? Wapas Karein ]"*
   * Tapping "Wapas Karein" reverts the action instantly.
2. **Zero Destructive Deletion Without Friendly Confirmation**:
   * Never use a bare trashcan icon.
   * Confirmations use gentle, conversational wording:
     > *"Kya aap ise hatana chahte hain? (Haan / Nahi)"*
3. **No Passwords or Complex Logins**:
   * Biometric fingerprint or a simple 4-digit PIN is optional only for the *Gupt Tijori* (Secret Savings Vault).
