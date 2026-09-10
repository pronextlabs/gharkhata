package com.gharkhata.app.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        TransactionEntity::class,
        MilkLogEntity::class,
        StaffEntity::class,
        StaffAttendanceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GharKhataDatabase : RoomDatabase() {
    abstract fun dao(): GharKhataDao

    companion object {
        @Volatile
        private var INSTANCE: GharKhataDatabase? = null

        fun getInstance(context: Context): GharKhataDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GharKhataDatabase::class.java,
                    "gharkhata.db"
                )
                    .enableMultiInstanceInvalidation()
                    // Disallow destructive migrations to protect household history
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
