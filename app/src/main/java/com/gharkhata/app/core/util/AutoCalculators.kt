package com.gharkhata.app.core.util

import com.gharkhata.app.domain.model.CashGalla
import com.gharkhata.app.domain.model.MilkBottleLog
import java.text.DecimalFormat

object AutoCalculators {

    /**
     * Calculates today's safe spending limit based on remaining days in the month.
     * Formula: (MonthlyBudget - SpentSoFar) / DaysRemaining
     */
    fun calculateDailySafeSpend(
        monthlyBudgetInr: Long,
        totalSpentInr: Long,
        daysInMonth: Int,
        currentDayOfMonth: Int
    ): Long {
        val remainingDays = (daysInMonth - currentDayOfMonth + 1).coerceAtLeast(1)
        val remainingBudget = (monthlyBudgetInr - totalSpentInr).coerceAtLeast(0)
        return (remainingBudget / remainingDays)
    }

    /**
     * Calculates month-end milk total bill.
     */
    fun calculateMilkTotal(logs: List<MilkBottleLog>, ratePerLiter: Int): Pair<Double, Long> {
        val totalLiters = logs.sumOf { it.status.liters }
        val totalBill = (totalLiters * ratePerLiter).toLong()
        return Pair(totalLiters, totalBill)
    }

    /**
     * Calculates domestic staff net payable on the 1st of the month.
     * Prorates based on present and half days, deducting advances.
     */
    fun calculateStaffPayroll(
        monthlySalaryInr: Long,
        daysInMonth: Int,
        presentDays: Int,
        halfDays: Int = 0,
        advanceTakenInr: Long = 0
    ): Long {
        if (daysInMonth <= 0) return 0
        val effectivePresentDays = presentDays.toDouble() + (halfDays * 0.5)
        val perDayRate = monthlySalaryInr.toDouble() / daysInMonth.toDouble()
        val earnedSalary = (perDayRate * effectivePresentDays).toLong()
        return (earnedSalary - advanceTakenInr).coerceAtLeast(0)
    }

    /**
     * Calculates physical cash notes in hand.
     */
    fun calculateCashGalla(galla: CashGalla): Long {
        return galla.totalCashInr
    }

    /**
     * Formats numbers into the standard Indian Rupee numbering format (₹ Lakhs, Crores)
     * e.g., 145000 -> "₹ 1,45,000"
     */
    fun formatInr(amount: Long): String {
        if (amount == 0L) return "₹ 0"
        val isNegative = amount < 0
        val absVal = kotlin.math.abs(amount).toString()

        val formatted = if (absVal.length <= 3) {
            absVal
        } else {
            val lastThree = absVal.substring(absVal.length - 3)
            val remaining = absVal.substring(0, absVal.length - 3)
            val builder = StringBuilder()
            var count = 0
            for (i in remaining.length - 1 downTo 0) {
                builder.append(remaining[i])
                count++
                if (count % 2 == 0 && i > 0) {
                    builder.append(",")
                }
            }
            builder.reverse().append(",").append(lastThree).toString()
        }

        return if (isNegative) "-₹ $formatted" else "₹ $formatted"
    }

    /**
     * Generates a clean, friendly WhatsApp message for the milkman.
     */
    fun generateMilkWhatsAppSlip(
        monthName: String,
        totalLiters: Double,
        ratePerLiter: Int,
        totalInr: Long
    ): String {
        val df = DecimalFormat("#.#")
        return """
            🥛 *Doodh Ka Hisaab (${monthName})*
            ------------------------------------
            • Kul Doodh: ${df.format(totalLiters)} Litres
            • Rate: ₹ ${ratePerLiter} / Litre
            • *Kul Rakam (Total): ₹ ${formatInr(totalInr).replace("₹ ", "")}*
            ------------------------------------
            Bheja gaya via *GharKhata* (100% Free & Private)
        """.trimIndent()
    }

    /**
     * Generates a clean, friendly WhatsApp salary slip for domestic helpers.
     */
    fun generateStaffSalarySlip(
        staffName: String,
        monthName: String,
        baseSalaryInr: Long,
        absentDays: Int,
        advanceInr: Long,
        netPayableInr: Long
    ): String {
        return """
            🧹 *Salary Slip: ${staffName}* (${monthName})
            ------------------------------------
            • Tai Salary: ${formatInr(baseSalaryInr)}
            • Chhutti: ${absentDays} din
            • Advance Diya Tha: ${formatInr(advanceInr)}
            ------------------------------------
            • *Haath Mein Milne Wali Salary: ${formatInr(netPayableInr)}*
            ------------------------------------
            Bheja gaya via *GharKhata* (100% Free & Private)
        """.trimIndent()
    }

    /**
     * Evaluates a Mandi math expression containing numbers and '+' / '-' operators.
     * e.g., "40+60+35" -> 135
     * e.g., "100-20" -> 80
     * e.g., "150" -> 150
     */
    fun evaluateMandiExpression(expression: String): Long {
        if (expression.isBlank()) return 0L
        try {
            val sanitized = expression.replace(" ", "")
            val tokens = mutableListOf<String>()
            val currentNumber = StringBuilder()
            for (ch in sanitized) {
                if (ch == '+' || ch == '-') {
                    if (currentNumber.isNotEmpty()) {
                        tokens.add(currentNumber.toString())
                        currentNumber.clear()
                    }
                    tokens.add(ch.toString())
                } else if (ch.isDigit()) {
                    currentNumber.append(ch)
                }
            }
            if (currentNumber.isNotEmpty()) {
                tokens.add(currentNumber.toString())
            }
            if (tokens.isEmpty()) return 0L
            var result = tokens[0].toLongOrNull() ?: 0L
            var i = 1
            while (i < tokens.size) {
                val op = tokens[i]
                val nextVal = if (i + 1 < tokens.size) tokens[i + 1].toLongOrNull() ?: 0L else 0L
                if (op == "+") {
                    result += nextVal
                } else if (op == "-") {
                    result -= nextVal
                }
                i += 2
            }
            return result.coerceAtLeast(0L)
        } catch (_: Exception) {
            return 0L
        }
    }
}
