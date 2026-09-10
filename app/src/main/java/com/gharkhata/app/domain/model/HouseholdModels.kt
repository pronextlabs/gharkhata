package com.gharkhata.app.domain.model

enum class MilkStatus(val liters: Double, val label: String) {
    NO_MILK(0.0, "0 L"),
    FULL_LITER(1.0, "1.0 L"),
    LITER_AND_HALF(1.5, "1.5 L"),
    TWO_LITERS(2.0, "2.0 L");

    fun next(): MilkStatus = when (this) {
        NO_MILK -> FULL_LITER
        FULL_LITER -> LITER_AND_HALF
        LITER_AND_HALF -> TWO_LITERS
        TWO_LITERS -> NO_MILK
    }
}

data class MilkBottleLog(
    val id: Long = 0,
    val dayOfMonth: Int,
    val dateEpochDay: Long,
    val status: MilkStatus = MilkStatus.NO_MILK,
    val ratePerLiterInr: Int = 66
) {
    val costInr: Long
        get() = (status.liters * ratePerLiterInr).toLong()
}

enum class AttendanceStatus(val labelEn: String, val labelHi: String) {
    PRESENT("Present", "आई"),
    ABSENT("Absent", "छुट्टी"),
    HALF_DAY("Half Day", "आधा दिन");

    fun next(): AttendanceStatus = when (this) {
        PRESENT -> ABSENT
        ABSENT -> HALF_DAY
        HALF_DAY -> PRESENT
    }
}

data class StaffMember(
    val id: Long = 0,
    val name: String,
    val role: String = "Maid / Jhadu Pocha",
    val monthlySalaryInr: Long,
    val advanceBalanceInr: Long = 0,
    val isActive: Boolean = true
)

data class StaffAttendance(
    val id: Long = 0,
    val staffId: Long,
    val dayOfMonth: Int,
    val dateEpochDay: Long,
    val status: AttendanceStatus = AttendanceStatus.PRESENT
)

data class CylinderStatus(
    val id: Long = 0,
    val connectedDateEpochDay: Long,
    val daysElapsed: Int,
    val averageLifespanDays: Int = 36,
    val costInr: Long = 850
) {
    val daysRemaining: Int
        get() = (averageLifespanDays - daysElapsed).coerceAtLeast(0)

    val percentRemaining: Float
        get() = (daysRemaining.toFloat() / averageLifespanDays.toFloat()).coerceIn(0f, 1f)

    val needsRefillBooking: Boolean
        get() = daysRemaining <= 5
}

data class CashGalla(
    val note500: Int = 0,
    val note200: Int = 0,
    val note100: Int = 0,
    val note50: Int = 0,
    val note20: Int = 0,
    val note10: Int = 0
) {
    val totalCashInr: Long
        get() = (note500 * 500L) +
                (note200 * 200L) +
                (note100 * 100L) +
                (note50 * 50L) +
                (note20 * 20L) +
                (note10 * 10L)
}
