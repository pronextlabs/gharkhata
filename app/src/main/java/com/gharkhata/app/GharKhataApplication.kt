package com.gharkhata.app

import android.app.Application
import com.gharkhata.app.core.database.GharKhataDatabase

class GharKhataApplication : Application() {
    val database: GharKhataDatabase by lazy {
        GharKhataDatabase.getInstance(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}
