package com.gharkhata.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gharkhata.app.R
import com.gharkhata.app.core.designsystem.GharKhataColors
import com.gharkhata.app.core.designsystem.tactileClick
import com.gharkhata.app.core.util.AutoCalculators
import com.gharkhata.app.domain.model.CategoryType
import java.time.LocalDate

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    initialBudgetInr: Long = 0L,
    onSaveTransaction: (CategoryType, Long) -> Unit = { _, _ -> }
) {
    val today = remember { LocalDate.now() }
    val daysInMonth = remember { today.lengthOfMonth() }
    val currentDay = remember { today.dayOfMonth }

    var monthlyBudget by remember { mutableStateOf(initialBudgetInr) }
    var totalSpent by remember { mutableStateOf(0L) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var budgetInput by remember { mutableStateOf("") }

    val dailySafeSpend = remember(monthlyBudget, totalSpent) {
        AutoCalculators.calculateDailySafeSpend(monthlyBudget, totalSpent, daysInMonth, currentDay)
    }

    var selectedCategory by remember { mutableStateOf(CategoryType.VEGETABLES) }
    var inputExpression by remember { mutableStateOf("") }
    var lastSavedMessage by remember { mutableStateOf<String?>(null) }

    val computedAmount = remember(inputExpression) {
        try {
            if (inputExpression.isEmpty()) 0L
            else {
                val parts = inputExpression.split("+").map { it.trim() }.filter { it.isNotEmpty() }
                parts.sumOf { it.toLongOrNull() ?: 0L }
            }
        } catch (_: Exception) {
            0L
        }
    }

    // Budget Configuration Dialog
    if (showBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title = { Text(text = "Monthly Budget Set Karein", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = budgetInput,
                    onValueChange = { budgetInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Kul Mahine Ka Budget (₹)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GharKhataColors.BrandTerracotta,
                        cursorColor = GharKhataColors.BrandTerracotta
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        monthlyBudget = budgetInput.toLongOrNull() ?: 0L
                        showBudgetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GharKhataColors.BrandTerracotta)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) {
                    Text("Cancel", color = GharKhataColors.TextSecondary)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GharKhataColors.CanvasBone)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // --- Branded Top Header with App Logo ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = "GharKhata Logo",
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Column {
                    Text(
                        text = "GharKhata",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = GharKhataColors.BrandTerracotta
                    )
                    Text(
                        text = "Apka Apna Hisaab Kitab",
                        fontSize = 11.sp,
                        color = GharKhataColors.TextSecondary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GharKhataColors.IncomeGreenLight,
                border = BorderStroke(1.dp, GharKhataColors.IncomeGreen.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(GharKhataColors.IncomeGreen)
                    )
                    Text(
                        text = "100% Offline",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GharKhataColors.IncomeGreen
                    )
                }
            }
        }

        // --- Header Pacer Card (Click to Set/Edit Budget) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .tactileClick {
                    budgetInput = if (monthlyBudget > 0L) monthlyBudget.toString() else ""
                    showBudgetDialog = true
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GharKhataColors.BrandTerracotta),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Aaj Ka Safe Kharcha",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = AutoCalculators.formatInr(dailySafeSpend),
                    color = Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (monthlyBudget > 0L)
                        "${daysInMonth - currentDay + 1} din bache hain (Budget: ${AutoCalculators.formatInr(monthlyBudget)})"
                    else
                        "Tap karein mahine ka budget set karne ke liye",
                    color = Color.White.copy(alpha = 0.80f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- Category Selector (Big 64dp Visual Tiles) ---
        Text(
            text = "Kahan Kharcha Hua?",
            color = GharKhataColors.TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CategoryType.entries.toTypedArray()) { cat ->
                val isSelected = cat == selectedCategory
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) GharKhataColors.BrandTerracottaLight else GharKhataColors.SurfaceCard)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) GharKhataColors.BrandTerracotta else GharKhataColors.BorderLight,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .tactileClick { selectedCategory = cat }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = cat.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = cat.titleEn.substringBefore(" "),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) GharKhataColors.BrandTerracotta else GharKhataColors.TextPrimary,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- Mandi Calculator Keypad Display ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GharKhataColors.SurfaceCard)
                .border(1.dp, GharKhataColors.BorderLight, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${selectedCategory.emoji} ${selectedCategory.titleEn}",
                color = GharKhataColors.TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (inputExpression.isEmpty()) "₹ 0" else "₹ $inputExpression",
                color = GharKhataColors.TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- Keypad Buttons (Thumb-Zone, 0ms render, Instant Haptic) ---
        val keypadRows = listOf(
            listOf("1", "2", "3", "+"),
            listOf("4", "5", "6", "-"),
            listOf("7", "8", "9", "⌫"),
            listOf("C", "0", "00", "✓")
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (row in keypadRows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (key in row) {
                        val isActionKey = key == "✓"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isActionKey) GharKhataColors.IncomeGreen
                                    else if (key == "+" || key == "-" || key == "⌫" || key == "C") GharKhataColors.SurfaceSubtle
                                    else GharKhataColors.SurfaceCard
                                )
                                .border(
                                    1.dp,
                                    if (isActionKey) GharKhataColors.IncomeGreen else GharKhataColors.BorderLight,
                                    RoundedCornerShape(10.dp)
                                )
                                .tactileClick {
                                    when (key) {
                                        "C" -> inputExpression = ""
                                        "⌫" -> if (inputExpression.isNotEmpty()) inputExpression = inputExpression.dropLast(1)
                                        "✓" -> {
                                            if (computedAmount > 0) {
                                                onSaveTransaction(selectedCategory, computedAmount)
                                                totalSpent += computedAmount
                                                lastSavedMessage = "₹$computedAmount jod diya gaya!"
                                                inputExpression = ""
                                            }
                                        }
                                        else -> inputExpression += key
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                color = if (isActionKey) Color.White else GharKhataColors.TextPrimary,
                                fontSize = if (isActionKey) 22.sp else 20.sp,
                                fontWeight = if (isActionKey) FontWeight.Bold else FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // --- 5-Second Feedback ---
        lastSavedMessage?.let { msg ->
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(GharKhataColors.IncomeGreenLight)
                    .border(1.dp, GharKhataColors.IncomeGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "✓ $msg", color = GharKhataColors.IncomeGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                TextButton(onClick = { lastSavedMessage = null }) {
                    Text(text = "Wapas Karein", color = GharKhataColors.IncomeGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
