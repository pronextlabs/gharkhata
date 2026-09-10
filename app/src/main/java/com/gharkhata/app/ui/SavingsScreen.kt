package com.gharkhata.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gharkhata.app.core.designsystem.GharKhataColors
import com.gharkhata.app.core.designsystem.tactileClick
import com.gharkhata.app.core.util.AutoCalculators
import com.gharkhata.app.domain.model.CashGalla

@Composable
fun SavingsScreen(
    modifier: Modifier = Modifier
) {
    var isTijoriUnlocked by remember { mutableStateOf(false) }
    var tijoriAmount by remember { mutableStateOf(8500L) }

    var n500 by remember { mutableStateOf(4) }
    var n200 by remember { mutableStateOf(3) }
    var n100 by remember { mutableStateOf(8) }
    var n50 by remember { mutableStateOf(6) }

    val galla = remember(n500, n200, n100, n50) {
        CashGalla(note500 = n500, note200 = n200, note100 = n100, note50 = n50)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GharKhataColors.CanvasBone)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- Gupt Tijori (Biometric/PIN Vault) ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GharKhataColors.SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GharKhataColors.BorderLight))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔒 Gupt Tijori (Private Vault)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GharKhataColors.TextPrimary)
                        TextButton(onClick = { isTijoriUnlocked = !isTijoriUnlocked }) {
                            Text(text = if (isTijoriUnlocked) "Chhupayein (Lock)" else "Kholein (Unlock)", fontSize = 12.sp, color = GharKhataColors.BrandTerracotta)
                        }
                    }

                    if (isTijoriUnlocked) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Aapki Apni Bachat (Stree Dhan):", fontSize = 13.sp, color = GharKhataColors.TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = AutoCalculators.formatInr(tijoriAmount), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = GharKhataColors.IncomeGreen)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Yeh hisaab kisi bhi report ya summary mein nahi dikhega.", fontSize = 11.sp, color = GharKhataColors.TextMuted)
                    } else {
                        Text(text = "Tap 'Kholein' to view your confidential emergency savings.", fontSize = 12.sp, color = GharKhataColors.TextMuted)
                    }
                }
            }
        }

        // --- Galla: Cash Note Denomination Counter ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GharKhataColors.SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GharKhataColors.BorderLight))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💵 Galla (Physical Cash in Hand)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GharKhataColors.TextPrimary)
                        Text(text = AutoCalculators.formatInr(galla.totalCashInr), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GharKhataColors.BrandTerracotta)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = GharKhataColors.BorderLight)

                    // Steppers
                    DenominationRow(label = "₹ 500", count = n500, onMinus = { if (n500 > 0) n500-- }, onPlus = { n500++ })
                    DenominationRow(label = "₹ 200", count = n200, onMinus = { if (n200 > 0) n200-- }, onPlus = { n200++ })
                    DenominationRow(label = "₹ 100", count = n100, onMinus = { if (n100 > 0) n100-- }, onPlus = { n100++ })
                    DenominationRow(label = "₹ 50", count = n50, onMinus = { if (n50 > 0) n50-- }, onPlus = { n50++ })
                }
            }
        }

        // --- Kameti / Chit Fund Card ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GharKhataColors.SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GharKhataColors.BorderLight))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "🤝 Kitty / Kameti Tracker", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GharKhataColors.TextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Society Monthly Kameti: ₹2,000 / month", fontSize = 13.sp, color = GharKhataColors.TextSecondary)
                    Text(text = "Kist: 4 of 10 Paid (Agli kist: 5th Nov)", fontSize = 12.sp, color = GharKhataColors.TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Aapki baari: December 2026 (₹20,000 pot)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GharKhataColors.IncomeGreen)
                }
            }
        }
    }
}

@Composable
private fun DenominationRow(
    label: String,
    count: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onMinus,
                modifier = Modifier.size(32.dp),
                contentPadding = PaddingValues(0.dp)
            ) { Text("-") }
            Text(text = "$count", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = onPlus,
                modifier = Modifier.size(32.dp),
                contentPadding = PaddingValues(0.dp)
            ) { Text("+") }
        }
    }
}
