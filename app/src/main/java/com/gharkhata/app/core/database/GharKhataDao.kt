package com.gharkhata.app.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GharKhataDao {

    // --- Transactions ---
    @Query("SELECT * FROM transactions WHERE isPrivate = 0 ORDER BY createdAt DESC")
    fun getAllPublicTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE dateEpochDay = :epochDay AND isPrivate = 0 ORDER BY createdAt DESC")
    fun getTransactionsForDay(epochDay: Long): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amountInr) FROM transactions WHERE dateEpochDay >= :startEpochDay AND dateEpochDay <= :endEpochDay AND isPrivate = 0")
    fun getMonthlySpendSum(startEpochDay: Long, endEpochDay: Long): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    // --- Milk Logs ---
    @Query("SELECT * FROM milk_logs ORDER BY dateEpochDay ASC")
    fun getAllMilkLogs(): Flow<List<MilkLogEntity>>

    @Query("SELECT * FROM milk_logs WHERE dateEpochDay = :epochDay LIMIT 1")
    suspend fun getMilkLogForDay(epochDay: Long): MilkLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMilkLog(log: MilkLogEntity): Long

    // --- Staff & Attendance ---
    @Query("SELECT * FROM staff_members WHERE isActive = 1")
    fun getActiveStaff(): Flow<List<StaffEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffEntity): Long

    @Query("SELECT * FROM staff_attendance WHERE staffId = :staffId")
    fun getAttendanceForStaff(staffId: Long): Flow<List<StaffAttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendance(attendance: StaffAttendanceEntity): Long

    @Query("UPDATE staff_members SET advanceBalanceInr = advanceBalanceInr + :advanceAmount WHERE id = :staffId")
    suspend fun addAdvance(staffId: Long, advanceAmount: Long)
}
