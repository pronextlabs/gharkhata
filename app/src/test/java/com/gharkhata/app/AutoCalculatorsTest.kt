package com.gharkhata.app

import com.gharkhata.app.core.util.AutoCalculators
import com.gharkhata.app.domain.model.CashGalla
import com.gharkhata.app.domain.model.MilkBottleLog
import com.gharkhata.app.domain.model.MilkStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoCalculatorsTest {

    @Test
    fun testDailySafeSpendCalculation() {
        // Budget: 30,000. Spent: 15,000. Month days: 30. Current day: 16 (15 days left)
        // 15,000 / 15 = 1,000 / day
        val safeSpend = AutoCalculators.calculateDailySafeSpend(
            monthlyBudgetInr = 30000L,
            totalSpentInr = 15000L,
            daysInMonth = 30,
            currentDayOfMonth = 16
        )
        assertEquals(1000L, safeSpend)

        // When overspent, return 0 (no negative crashes)
        val overspent = AutoCalculators.calculateDailySafeSpend(
            monthlyBudgetInr = 20000L,
            totalSpentInr = 25000L,
            daysInMonth = 30,
            currentDayOfMonth = 15
        )
        assertEquals(0L, overspent)
    }

    @Test
    fun testMilkTotalCalculation() {
        val logs = listOf(
            MilkBottleLog(dayOfMonth = 1, dateEpochDay = 1, status = MilkStatus.FULL_LITER),       // 1.0 L
            MilkBottleLog(dayOfMonth = 2, dateEpochDay = 2, status = MilkStatus.LITER_AND_HALF),   // 1.5 L
            MilkBottleLog(dayOfMonth = 3, dateEpochDay = 3, status = MilkStatus.TWO_LITERS),       // 2.0 L
            MilkBottleLog(dayOfMonth = 4, dateEpochDay = 4, status = MilkStatus.NO_MILK)           // 0.0 L
        )
        // Total = 4.5 Liters @ ₹66 = ₹297
        val (liters, bill) = AutoCalculators.calculateMilkTotal(logs, ratePerLiter = 66)
        assertEquals(4.5, liters, 0.001)
        assertEquals(297L, bill)
    }

    @Test
    fun testStaffPayrollCalculation() {
        // Monthly Salary: 3000, Days: 30 (Rate = 100/day). Present: 28 days (Earned: 2800). Advance: 500.
        // Net: 2800 - 500 = 2300
        val netPayable = AutoCalculators.calculateStaffPayroll(
            monthlySalaryInr = 3000L,
            daysInMonth = 30,
            presentDays = 28,
            halfDays = 0,
            advanceTakenInr = 500L
        )
        assertEquals(2300L, netPayable)
    }

    @Test
    fun testCashGallaCalculation() {
        val galla = CashGalla(
            note500 = 4, // 2000
            note200 = 5, // 1000
            note100 = 8, // 800
            note50 = 4   // 200
        )
        // Total = 4000
        val total = AutoCalculators.calculateCashGalla(galla)
        assertEquals(4000L, total)
    }

    @Test
    fun testInrCurrencyFormatting() {
        assertEquals("₹ 0", AutoCalculators.formatInr(0L))
        assertEquals("₹ 450", AutoCalculators.formatInr(450L))
        assertEquals("₹ 4,500", AutoCalculators.formatInr(4500L))
        assertEquals("₹ 45,000", AutoCalculators.formatInr(45000L))
        assertEquals("₹ 1,45,000", AutoCalculators.formatInr(145000L))
        assertEquals("₹ 12,50,000", AutoCalculators.formatInr(1250000L))
    }

    @Test
    fun testWhatsAppSlipGenerators() {
        val milkSlip = AutoCalculators.generateMilkWhatsAppSlip("October", 28.5, 66, 1881L)
        assertTrue(milkSlip.contains("28.5 Litres"))
        assertTrue(milkSlip.contains("₹ 1,881"))
        assertTrue(milkSlip.contains("GharKhata"))

        val staffSlip = AutoCalculators.generateStaffSalarySlip("Sunita Bai", "October", 4000L, 2, 1000L, 2740L)
        assertTrue(staffSlip.contains("Sunita Bai"))
        assertTrue(staffSlip.contains("₹ 2,740"))
    }

    @Test
    fun testEvaluateMandiExpression() {
        assertEquals(0L, AutoCalculators.evaluateMandiExpression(""))
        assertEquals(0L, AutoCalculators.evaluateMandiExpression("   "))
        assertEquals(150L, AutoCalculators.evaluateMandiExpression("150"))
        assertEquals(135L, AutoCalculators.evaluateMandiExpression("40+60+35"))
        assertEquals(80L, AutoCalculators.evaluateMandiExpression("100-20"))
        assertEquals(120L, AutoCalculators.evaluateMandiExpression("40 + 60 + 20"))
        assertEquals(50L, AutoCalculators.evaluateMandiExpression("50+"))
        assertEquals(0L, AutoCalculators.evaluateMandiExpression("0"))
        assertEquals(200L, AutoCalculators.evaluateMandiExpression("150+100-50"))
        assertEquals(0L, AutoCalculators.evaluateMandiExpression("invalid"))
        assertEquals(75L, AutoCalculators.evaluateMandiExpression("  25 + 50  "))
    }

    @Test
    fun testDailySafeSpendWithMonthCumulativeSpending() {
        // ₹30,000 budget, spent ₹21,000 so far over first 20 days of 30-day month (11 days remaining: 20..30)
        // Remaining budget = 9,000 / 11 = 818
        val safeSpend = AutoCalculators.calculateDailySafeSpend(
            monthlyBudgetInr = 30000L,
            totalSpentInr = 21000L,
            daysInMonth = 30,
            currentDayOfMonth = 20
        )
        assertEquals(818L, safeSpend)

        // Last day of month (currentDay = 30, daysInMonth = 30) -> remainingDays = 1
        // Remaining budget = 1,500 / 1 = 1,500
        val lastDaySpend = AutoCalculators.calculateDailySafeSpend(
            monthlyBudgetInr = 25000L,
            totalSpentInr = 23500L,
            daysInMonth = 30,
            currentDayOfMonth = 30
        )
        assertEquals(1500L, lastDaySpend)
    }
}

