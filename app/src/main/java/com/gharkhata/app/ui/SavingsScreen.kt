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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gharkhata.app.core.designsystem.GharKhataColors
import com.gharkhata.app.core.util.AutoCalculators
import com.gharkhata.app.domain.model.CashGalla

import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.gharkhata.app.core.util.BiometricAuthHelper
import com.gharkhata.app.core.util.GharKhataPreferences

@Composable
fun SavingsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isTijoriUnlocked by remember { mutableStateOf(false) }
    var tijoriAmount by remember { mutableStateOf(GharKhataPreferences.getTijoriAmount(context)) }
    var showTijoriDialog by remember { mutableStateOf(false) }
    var tijoriInput by remember { mutableStateOf("") }

    val initialCounts = remember { GharKhataPreferences.getCashCounts(context) }
    var n500 by remember { mutableStateOf(initialCounts[0]) }
    var n200 by remember { mutableStateOf(initialCounts[1]) }
    var n100 by remember { mutableStateOf(initialCounts[2]) }
    var n50 by remember { mutableStateOf(initialCounts[3]) }

    val updateCash = { c500: Int, c200: Int, c100: Int, c50: Int ->
        n500 = c500
        n200 = c200
        n100 = c100
        n50 = c50
        GharKhataPreferences.setCashCounts(context, c500, c200, c100, c50)
    }

    val galla = remember(n500, n200, n100, n50) {
        CashGalla(note500 = n500, note200 = n200, note100 = n100, note50 = n50)
    }

    if (showTijoriDialog) {
        AlertDialog(
            onDismissRequest = { showTijoriDialog = false },
            title = { Text("Tijori Ki Rakam Set Karein", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = tijoriInput,
                    onValueChange = { tijoriInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Kul Bachat (₹)") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newAmount = tijoriInput.toLongOrNull() ?: 0L
                        tijoriAmount = newAmount
                        GharKhataPreferences.setTijoriAmount(context, newAmount)
                        showTijoriDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GharKhataColors.BrandTerracotta)
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showTijoriDialog = false }) { Text("Cancel") }
            }
        )
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
                        TextButton(onClick = {
                            if (isTijoriUnlocked) {
                                isTijoriUnlocked = false
                            } else {
                                val activity = context as? FragmentActivity
                                if (activity != null) {
                                    BiometricAuthHelper.authenticate(
                                        activity = activity,
                                        title = "Gupt Tijori Kholein",
                                        subtitle = "Biometric ya Device PIN se unlock karein",
                                        onSuccess = { isTijoriUnlocked = true },
                                        onError = { /* Keep locked */ }
                                    )
                                } else {
                                    isTijoriUnlocked = true
                                }
                            }
                        }) {
                            Text(text = if (isTijoriUnlocked) "Chhupayein (Lock)" else "Kholein (Unlock)", fontSize = 12.sp, color = GharKhataColors.BrandTerracotta)
                        }
                    }

                    if (isTijoriUnlocked) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Aapki Apni Bachat (Stree Dhan):", fontSize = 13.sp, color = GharKhataColors.TextSecondary)
                                Text(text = AutoCalculators.formatInr(tijoriAmount), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = GharKhataColors.IncomeGreen)
                            }
                            Button(
                                onClick = {
                                    tijoriInput = if (tijoriAmount > 0) tijoriAmount.toString() else ""
                                    showTijoriDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(if (tijoriAmount > 0) "Update" else "+ Rakam Dalein", fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Yeh hisaab kisi bhi report ya WhatsApp summary mein nahi dikhega.", fontSize = 11.sp, color = GharKhataColors.TextMuted)
                    } else {
                        Text(text = "Tap 'Kholein' to view your confidential emergency savings.", fontSize = 12.sp, color = GharKhataColors.TextMuted)
                    }
                }
            }
        }

        // --- Galla: Cash Note Denomination Counter (Starts at 0) ---
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

                    DenominationRow(
                        label = "₹ 500",
                        count = n500,
                        onMinus = { if (n500 > 0) updateCash(n500 - 1, n200, n100, n50) },
                        onPlus = { updateCash(n500 + 1, n200, n100, n50) }
                    )
                    DenominationRow(
                        label = "₹ 200",
                        count = n200,
                        onMinus = { if (n200 > 0) updateCash(n500, n200 - 1, n100, n50) },
                        onPlus = { updateCash(n500, n200 + 1, n100, n50) }
                    )
                    DenominationRow(
                        label = "₹ 100",
                        count = n100,
                        onMinus = { if (n100 > 0) updateCash(n500, n200, n100 - 1, n50) },
                        onPlus = { updateCash(n500, n200, n100 + 1, n50) }
                    )
                    DenominationRow(
                        label = "₹ 50",
                        count = n50,
                        onMinus = { if (n50 > 0) updateCash(n500, n200, n100, n50 - 1) },
                        onPlus = { updateCash(n500, n200, n100, n50 + 1) }
                    )
                }
            }
        }

        // --- Kameti / Chit Fund Card (Clean Unfilled State) ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GharKhataColors.SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GharKhataColors.BorderLight))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "🤝 Kitty / Kameti Tracker", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GharKhataColors.TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Mahine ki kameti kist aur payout track karne ke liye save karein.", fontSize = 12.sp, color = GharKhataColors.TextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { /* Hook for custom Kameti configuration */ },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ Kameti Jodein", fontSize = 12.sp, color = GharKhataColors.BrandTerracotta)
                    }
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
