package com.gharkhata.app.core.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [Index(value = ["dateEpochDay"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountInr: Long,
    val categoryId: Int,
    val paymentMode: String = "CASH",
    val note: String = "",
    val dateEpochDay: Long,
    val isPrivate: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "milk_logs",
    indices = [Index(value = ["dateEpochDay"], unique = true)]
)
data class MilkLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val dayOfMonth: Int,
    val liters: Double,
    val ratePerLiterInr: Int = 66,
    val isSettled: Boolean = false
)

@Entity(tableName = "staff_members")
data class StaffEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val role: String,
    val monthlySalaryInr: Long,
    val advanceBalanceInr: Long = 0,
    val isActive: Boolean = true
)

@Entity(
    tableName = "staff_attendance",
    indices = [Index(value = ["staffId", "dateEpochDay"], unique = true)]
)
data class StaffAttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val staffId: Long,
    val dateEpochDay: Long,
    val dayOfMonth: Int,
    val status: String // 'PRESENT', 'ABSENT', 'HALF_DAY'
)
