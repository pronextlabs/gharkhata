package com.gharkhata.app.domain.model

import androidx.compose.ui.graphics.Color

enum class CategoryType(
    val id: Int,
    val titleEn: String,
    val titleHi: String,
    val emoji: String,
    val colorHex: Long
) {
    VEGETABLES(1, "Sabzi Mandi", "सब्जी मंडी", "🥦", 0xFF2E6F40),
    GROCERIES(2, "Kirana / Ration", "किराना / राशन", "🌾", 0xFFC45B3E),
    MILK(3, "Doodh & Dairy", "दूध और डेयरी", "🥛", 0xFF2563EB),
    MAID(4, "Kamwali / Bai", "कामवाली / बाई", "🧹", 0xFF7C3AED),
    GAS_UTILITY(5, "Gas & Bijli", "गैस और बिजली", "⛽", 0xFFD97706),
    KIDS(6, "Baccho Ki Padhai", "बच्चों की पढ़ाई", "📚", 0xFF0284C7),
    MEDICAL(7, "Dawai & Doctor", "दवाई और डॉक्टर", "💊", 0xFFDC2626),
    POOJA_SHAGUN(8, "Pooja & Shagun", "पूजा और शगुन", "🪔", 0xFFB45309);

    companion object {
        fun fromId(id: Int): CategoryType = entries.find { it.id == id } ?: GROCERIES
    }
}

enum class PaymentMode {
    CASH,
    ONLINE_UPI
}

data class TransactionItem(
    val id: Long = 0,
    val amountInr: Long,
    val category: CategoryType,
    val paymentMode: PaymentMode = PaymentMode.CASH,
    val note: String = "",
    val dateEpochDay: Long,
    val isPrivate: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
