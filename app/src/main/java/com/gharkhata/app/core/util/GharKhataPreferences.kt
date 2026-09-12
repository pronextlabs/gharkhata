package com.gharkhata.app.core.util

import android.content.Context
import android.content.SharedPreferences

object GharKhataPreferences {
    private const val PREFS_NAME = "gharkhata_secure_prefs"

    private const val KEY_MONTHLY_BUDGET = "key_monthly_budget"
    private const val KEY_TIJORI_AMOUNT = "key_tijori_amount"
    private const val KEY_NOTE_500 = "key_note_500"
    private const val KEY_NOTE_200 = "key_note_200"
    private const val KEY_NOTE_100 = "key_note_100"
    private const val KEY_NOTE_50 = "key_note_50"
    private const val KEY_LANGUAGE = "key_language"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getMonthlyBudget(context: Context): Long {
        return getPrefs(context).getLong(KEY_MONTHLY_BUDGET, 0L)
    }

    fun setMonthlyBudget(context: Context, budget: Long) {
        getPrefs(context).edit().putLong(KEY_MONTHLY_BUDGET, budget.coerceAtLeast(0L)).apply()
    }

    fun getTijoriAmount(context: Context): Long {
        return getPrefs(context).getLong(KEY_TIJORI_AMOUNT, 0L)
    }

    fun setTijoriAmount(context: Context, amount: Long) {
        getPrefs(context).edit().putLong(KEY_TIJORI_AMOUNT, amount.coerceAtLeast(0L)).apply()
    }

    fun getCashCounts(context: Context): IntArray {
        val prefs = getPrefs(context)
        return intArrayOf(
            prefs.getInt(KEY_NOTE_500, 0),
            prefs.getInt(KEY_NOTE_200, 0),
            prefs.getInt(KEY_NOTE_100, 0),
            prefs.getInt(KEY_NOTE_50, 0)
        )
    }

    fun setCashCounts(context: Context, n500: Int, n200: Int, n100: Int, n50: Int) {
        getPrefs(context).edit()
            .putInt(KEY_NOTE_500, n500.coerceAtLeast(0))
            .putInt(KEY_NOTE_200, n200.coerceAtLeast(0))
            .putInt(KEY_NOTE_100, n100.coerceAtLeast(0))
            .putInt(KEY_NOTE_50, n50.coerceAtLeast(0))
            .apply()
    }

    fun getLanguage(context: Context): String {
        return getPrefs(context).getString(KEY_LANGUAGE, "Hinglish") ?: "Hinglish"
    }

    fun setLanguage(context: Context, lang: String) {
        getPrefs(context).edit().putString(KEY_LANGUAGE, lang).apply()
    }
}
