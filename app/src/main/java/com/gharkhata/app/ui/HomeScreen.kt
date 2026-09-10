package com.gharkhata.app.ui

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gharkhata.app.R
import com.gharkhata.app.core.database.GharKhataDatabase
import com.gharkhata.app.core.database.TransactionEntity
import com.gharkhata.app.core.designsystem.GharKhataColors
import com.gharkhata.app.core.designsystem.tactileClick
import com.gharkhata.app.core.util.AutoCalculators
import com.gharkhata.app.domain.model.CategoryType
import com.gharkhata.app.domain.model.PaymentMode
import com.gharkhata.app.domain.model.TransactionItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    initialBudgetInr: Long = 0L,
    onSaveTransaction: (CategoryType, Long) -> Unit = { _, _ -> },
    onSaveTransactionWithMode: (CategoryType, Long, PaymentMode) -> Unit = { cat, amt, _ -> onSaveTransaction(cat, amt) },
    onDeleteTransaction: (Long) -> Unit = {}
) {
    val today = remember { LocalDate.now() }
    val daysInMonth = remember { today.lengthOfMonth() }
    val currentDay = remember { today.dayOfMonth }

    val context = LocalContext.current
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    val dao = remember {
        try {
            GharKhataDatabase.getInstance(context).dao()
        } catch (_: Exception) {
            null
        }
    }

    var monthlyBudget by remember { mutableStateOf(initialBudgetInr) }
    var totalSpent by remember { mutableStateOf(0L) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var budgetInput by remember { mutableStateOf("") }

    var selectedCategory by remember { mutableStateOf(CategoryType.VEGETABLES) }
    var selectedPaymentMode by remember { mutableStateOf(PaymentMode.CASH) }
    var inputExpression by remember { mutableStateOf("") }
    var lastSavedMessage by remember { mutableStateOf<String?>(null) }

    val todayTransactions = remember { mutableStateListOf<TransactionItem>() }

    // Observe today's transactions from Room SQLite if available
    LaunchedEffect(today) {
        dao?.let { d ->
            try {
                d.getTransactionsForDay(today.toEpochDay()).collect { entities ->
                    todayTransactions.clear()
                    todayTransactions.addAll(entities.map { it.toItem() })
                    totalSpent = entities.sumOf { it.amountInr }
                }
            } catch (_: Exception) {}
        }
    }

    val dailySafeSpend = remember(monthlyBudget, totalSpent) {
        AutoCalculators.calculateDailySafeSpend(monthlyBudget, totalSpent, daysInMonth, currentDay)
    }

    val computedAmount = remember(inputExpression) {
        AutoCalculators.evaluateMandiExpression(inputExpression)
    }

    val saveExpense = {
        if (computedAmount > 0) {
            val amount = computedAmount
            val cat = selectedCategory
            val mode = selectedPaymentMode
            val now = System.currentTimeMillis()
            val newTx = TransactionItem(
                id = now,
                amountInr = amount,
                category = cat,
                paymentMode = mode,
                dateEpochDay = today.toEpochDay(),
                createdAt = now
            )

            // If DAO is not present (e.g. tests), update in-memory list directly
            if (dao == null) {
                todayTransactions.add(0, newTx)
                totalSpent += amount
            } else {
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        dao.insertTransaction(
                            TransactionEntity(
                                amountInr = amount,
                                categoryId = cat.id,
                                paymentMode = mode.name,
                                dateEpochDay = today.toEpochDay(),
                                createdAt = now
                            )
                        )
                    } catch (_: Exception) {}
                }
            }

            onSaveTransactionWithMode(cat, amount, mode)
            lastSavedMessage = "₹$amount ${cat.titleEn.substringBefore(" ")} jod diya gaya!"
            inputExpression = ""
            try {
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            } catch (_: Exception) {}
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

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GharKhataColors.CanvasBone)
            .verticalScroll(scrollState)
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

        // --- Category Selector (2 Rows of 4 Big Visual Tiles) ---
        Text(
            text = "Kahan Kharcha Hua?",
            color = GharKhataColors.TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryType.entries.chunked(4).forEach { rowCats ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowCats.forEach { cat ->
                        val isSelected = cat == selectedCategory
                        Column(
                            modifier = Modifier
                                .weight(1f)
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
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Payment Mode Selector (Cash / UPI Pill Toggle) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(GharKhataColors.SurfaceSubtle)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedPaymentMode == PaymentMode.CASH) GharKhataColors.SurfaceCard else Color.Transparent)
                    .tactileClick { selectedPaymentMode = PaymentMode.CASH }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💵 Cash (Nagad)",
                    fontSize = 12.sp,
                    fontWeight = if (selectedPaymentMode == PaymentMode.CASH) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedPaymentMode == PaymentMode.CASH) GharKhataColors.BrandTerracotta else GharKhataColors.TextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedPaymentMode == PaymentMode.ONLINE_UPI) GharKhataColors.SurfaceCard else Color.Transparent)
                    .tactileClick { selectedPaymentMode = PaymentMode.ONLINE_UPI }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📱 Online / UPI",
                    fontSize = 12.sp,
                    fontWeight = if (selectedPaymentMode == PaymentMode.ONLINE_UPI) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedPaymentMode == PaymentMode.ONLINE_UPI) GharKhataColors.BrandTerracotta else GharKhataColors.TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

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
            Column {
                Text(
                    text = "${selectedCategory.emoji} ${selectedCategory.titleEn}",
                    color = GharKhataColors.TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                if (inputExpression.contains("+") || inputExpression.contains("-")) {
                    Text(
                        text = "= ${AutoCalculators.formatInr(computedAmount)}",
                        color = GharKhataColors.IncomeGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = if (inputExpression.isEmpty()) "₹ 0" else "₹ $inputExpression",
                color = GharKhataColors.TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                                .height(50.dp)
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
                                        "✓" -> saveExpense()
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

        // --- Primary Action Button: "Kharcha Jodein" (Step 3: Big Green Full-Width Button) ---
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { saveExpense() },
            enabled = computedAmount > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GharKhataColors.IncomeGreen,
                disabledContainerColor = GharKhataColors.IncomeGreen.copy(alpha = 0.35f)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "✓",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (computedAmount > 0)
                        "Kharcha Jodein • ${AutoCalculators.formatInr(computedAmount)}"
                    else
                        "Kharcha Jodein (Add Expense)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // --- 5-Second Feedback with Undo ("Wapas Karein") ---
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
                Text(
                    text = "✓ $msg",
                    color = GharKhataColors.IncomeGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                TextButton(onClick = {
                    if (todayTransactions.isNotEmpty()) {
                        val removed = todayTransactions.removeAt(0)
                        totalSpent = (totalSpent - removed.amountInr).coerceAtLeast(0L)
                        coroutineScope.launch(Dispatchers.IO) {
                            try {
                                dao?.deleteTransactionById(removed.id)
                            } catch (_: Exception) {}
                        }
                        onDeleteTransaction(removed.id)
                    }
                    lastSavedMessage = null
                }) {
                    Text(
                        text = "Wapas Karein",
                        color = GharKhataColors.IncomeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // --- Today's Expense Stream ("Aaj Ke Kharche") ---
        if (todayTransactions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aaj Ke Kharche (${todayTransactions.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GharKhataColors.TextPrimary
                )
                Text(
                    text = "Kul: ${AutoCalculators.formatInr(todayTransactions.sumOf { it.amountInr })}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GharKhataColors.BrandTerracotta
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                todayTransactions.forEach { tx ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = GharKhataColors.SurfaceCard),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(GharKhataColors.BorderLight)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = tx.category.emoji, fontSize = 22.sp)
                                Column {
                                    Text(
                                        text = tx.category.titleEn,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GharKhataColors.TextPrimary
                                    )
                                    Text(
                                        text = if (tx.paymentMode == PaymentMode.ONLINE_UPI) "Online / UPI" else "Cash",
                                        fontSize = 11.sp,
                                        color = GharKhataColors.TextSecondary
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "-${AutoCalculators.formatInr(tx.amountInr)}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GharKhataColors.ExpenseRed
                                )
                                IconButton(
                                    onClick = {
                                        todayTransactions.remove(tx)
                                        totalSpent = (totalSpent - tx.amountInr).coerceAtLeast(0L)
                                        coroutineScope.launch(Dispatchers.IO) {
                                            try {
                                                dao?.deleteTransactionById(tx.id)
                                            } catch (_: Exception) {}
                                        }
                                        onDeleteTransaction(tx.id)
                                        if (lastSavedMessage?.contains("₹${tx.amountInr}") == true) {
                                            lastSavedMessage = null
                                        }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text(
                                        text = "✕",
                                        fontSize = 14.sp,
                                        color = GharKhataColors.TextMuted,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
