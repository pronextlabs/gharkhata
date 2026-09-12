package com.gharkhata.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.platform.LocalContext
import com.gharkhata.app.core.database.GharKhataDatabase
import com.gharkhata.app.core.database.MilkLogEntity
import com.gharkhata.app.core.database.StaffEntity
import com.gharkhata.app.core.designsystem.GharKhataColors
import com.gharkhata.app.core.designsystem.tactileClick
import com.gharkhata.app.core.util.AutoCalculators
import com.gharkhata.app.domain.model.MilkBottleLog
import com.gharkhata.app.domain.model.MilkStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun ServicesScreen(
    modifier: Modifier = Modifier,
    onSendWhatsApp: (String) -> Unit = {}
) {
    var activeSubTab by remember { mutableStateOf("MILK") } // MILK or STAFF

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val dao = remember {
        try {
            GharKhataDatabase.getInstance(context).dao()
        } catch (_: Exception) {
            null
        }
    }

    val today = remember { LocalDate.now() }
    val daysInMonth = remember { today.lengthOfMonth() }
    val monthName = remember { today.month.name.lowercase().replaceFirstChar { it.uppercase() } }

    // Milk logs initialized to 0L (no fake pre-filled data)
    val milkLogs = remember {
        mutableStateListOf<MilkBottleLog>().apply {
            for (day in 1..daysInMonth) {
                val epoch = today.withDayOfMonth(day).toEpochDay()
                add(MilkBottleLog(dayOfMonth = day, dateEpochDay = epoch, status = MilkStatus.NO_MILK))
            }
        }
    }

    // Load persisted milk logs from Room SQLite
    LaunchedEffect(today) {
        dao?.let { d ->
            try {
                d.getAllMilkLogs().collect { entities ->
                    val map = entities.associateBy { it.dateEpochDay }
                    for (index in milkLogs.indices) {
                        val day = milkLogs[index].dayOfMonth
                        val epoch = today.withDayOfMonth(day).toEpochDay()
                        map[epoch]?.let { ent ->
                            val status = when {
                                ent.liters >= 2.0 -> MilkStatus.TWO_LITERS
                                ent.liters >= 1.5 -> MilkStatus.LITER_AND_HALF
                                ent.liters >= 1.0 -> MilkStatus.FULL_LITER
                                else -> MilkStatus.NO_MILK
                            }
                            milkLogs[index] = milkLogs[index].copy(status = status, dateEpochDay = epoch)
                        }
                    }
                }
            } catch (_: Exception) {}
        }
    }

    val (totalLiters, totalMilkBill) = remember(milkLogs.toList()) {
        AutoCalculators.calculateMilkTotal(milkLogs, ratePerLiter = 66)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GharKhataColors.CanvasBone)
            .padding(16.dp)
    ) {
        // --- Sub-Tab Selector (Pill Toggle) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GharKhataColors.SurfaceSubtle)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeSubTab == "MILK") GharKhataColors.SurfaceCard else Color.Transparent)
                    .tactileClick { activeSubTab = "MILK" }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🥛 Doodh Ka Hisaab",
                    fontSize = 13.sp,
                    fontWeight = if (activeSubTab == "MILK") FontWeight.Bold else FontWeight.Normal,
                    color = if (activeSubTab == "MILK") GharKhataColors.BrandTerracotta else GharKhataColors.TextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeSubTab == "STAFF") GharKhataColors.SurfaceCard else Color.Transparent)
                    .tactileClick { activeSubTab = "STAFF" }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🧹 Kamwali / Bai",
                    fontSize = 13.sp,
                    fontWeight = if (activeSubTab == "STAFF") FontWeight.Bold else FontWeight.Normal,
                    color = if (activeSubTab == "STAFF") GharKhataColors.BrandTerracotta else GharKhataColors.TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeSubTab == "MILK") {
            // --- Milk Tally Summary Card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GharKhataColors.SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GharKhataColors.BorderLight))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Kul Doodh ($monthName)", fontSize = 12.sp, color = GharKhataColors.TextSecondary)
                        Text(text = "%.1f L".format(totalLiters), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GharKhataColors.BrandTerracotta)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Kul Bill (@ ₹66/L)", fontSize = 12.sp, color = GharKhataColors.TextSecondary)
                        Text(text = AutoCalculators.formatInr(totalMilkBill), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GharKhataColors.TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- 1-Tap Calendar Grid with Bottle Symbols ---
            Text(text = "Din Par Tap Karein (1L -> 1.5L -> 2L -> 0L)", fontSize = 12.sp, color = GharKhataColors.TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(milkLogs.size) { index ->
                    val log = milkLogs[index]
                    val isZero = log.status == MilkStatus.NO_MILK
                    val isExtra = log.status == MilkStatus.LITER_AND_HALF || log.status == MilkStatus.TWO_LITERS

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isZero) GharKhataColors.SurfaceSubtle
                                else if (isExtra) GharKhataColors.BrandTerracottaLight
                                else GharKhataColors.SurfaceCard
                            )
                            .border(1.dp, GharKhataColors.BorderLight, RoundedCornerShape(8.dp))
                            .tactileClick {
                                val nextStatus = log.status.next()
                                val epoch = today.withDayOfMonth(log.dayOfMonth).toEpochDay()
                                milkLogs[index] = log.copy(status = nextStatus, dateEpochDay = epoch)
                                dao?.let { d ->
                                    coroutineScope.launch(Dispatchers.IO) {
                                        try {
                                            d.insertOrUpdateMilkLog(
                                                MilkLogEntity(
                                                    dateEpochDay = epoch,
                                                    dayOfMonth = log.dayOfMonth,
                                                    liters = nextStatus.liters,
                                                    ratePerLiterInr = 66
                                                )
                                            )
                                        } catch (_: Exception) {}
                                    }
                                }
                            }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "${log.dayOfMonth}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GharKhataColors.TextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isZero) "🥛" else "🥛",
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (isZero) "0L" else log.status.label.replace(" L", "L"),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isZero) GharKhataColors.TextMuted else GharKhataColors.TextPrimary
                        )
                    }
                }
            }

            // --- WhatsApp Button ---
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (totalLiters > 0.0) {
                        val msg = AutoCalculators.generateMilkWhatsAppSlip(monthName, totalLiters, 66, totalMilkBill)
                        onSendWhatsApp(msg)
                    }
                },
                enabled = totalLiters > 0.0,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GharKhataColors.IncomeGreen)
            ) {
                Text(
                    text = if (totalLiters > 0.0) "WhatsApp Par Doodhwale Ko Bhejein" else "Pehle Doodh Log Karein",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        } else {
            // --- Clean Staff Screen (Backed by Room SQLite) ---
            val activeStaffList = remember { mutableStateListOf<StaffEntity>() }
            var staffName by remember { mutableStateOf("") }
            var baseSalary by remember { mutableStateOf(0L) }
            var absentDays by remember { mutableStateOf(0) }
            var advanceTaken by remember { mutableStateOf(0L) }
            var showAddStaffDialog by remember { mutableStateOf(false) }

            var inputStaffName by remember { mutableStateOf("") }
            var inputSalary by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                dao?.let { d ->
                    try {
                        d.getActiveStaff().collect { list ->
                            activeStaffList.clear()
                            activeStaffList.addAll(list)
                            if (list.isNotEmpty()) {
                                staffName = list[0].name
                                baseSalary = list[0].monthlySalaryInr
                                advanceTaken = list[0].advanceBalanceInr
                            }
                        }
                    } catch (_: Exception) {}
                }
            }

            if (showAddStaffDialog) {
                AlertDialog(
                    onDismissRequest = { showAddStaffDialog = false },
                    title = { Text("Staff / Helper Jodein", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = inputStaffName,
                                onValueChange = { inputStaffName = it },
                                label = { Text("Naam (e.g. Maid, Cook)") },
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = inputSalary,
                                onValueChange = { inputSalary = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Monthly Salary (₹)") },
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (inputStaffName.isNotBlank() && inputSalary.isNotBlank()) {
                                    val sName = inputStaffName.trim()
                                    val sSalary = inputSalary.toLongOrNull() ?: 0L
                                    staffName = sName
                                    baseSalary = sSalary
                                    absentDays = 0
                                    advanceTaken = 0L
                                    showAddStaffDialog = false
                                    dao?.let { d ->
                                        coroutineScope.launch(Dispatchers.IO) {
                                            try {
                                                val existing = activeStaffList.firstOrNull()
                                                d.insertStaff(
                                                    StaffEntity(
                                                        id = existing?.id ?: 0L,
                                                        name = sName,
                                                        role = "Household Help",
                                                        monthlySalaryInr = sSalary,
                                                        advanceBalanceInr = 0L,
                                                        isActive = true
                                                    )
                                                )
                                            } catch (_: Exception) {}
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GharKhataColors.BrandTerracotta)
                        ) { Text("Save") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddStaffDialog = false }) { Text("Cancel") }
                    }
                )
            }

            if (baseSalary == 0L || staffName.isBlank()) {
                // Empty state: No hardcoded demo staff
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = GharKhataColors.SurfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GharKhataColors.BorderLight))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🧹", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Koi Staff / Kamwali Nahi Judi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Salary aur chhutti calculate karne ke liye staff jodein.", fontSize = 12.sp, color = GharKhataColors.TextSecondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                inputStaffName = ""
                                inputSalary = ""
                                showAddStaffDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GharKhataColors.BrandTerracotta)
                        ) {
                            Text("+ Staff Jodein")
                        }
                    }
                }
            } else {
                // Configured staff with zero mock leaves or advances
                val netPayable = remember(baseSalary, absentDays, advanceTaken) {
                    AutoCalculators.calculateStaffPayroll(
                        monthlySalaryInr = baseSalary,
                        daysInMonth = daysInMonth,
                        presentDays = daysInMonth - absentDays,
                        advanceTakenInr = advanceTaken
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = GharKhataColors.SurfaceCard),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GharKhataColors.BorderLight))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = staffName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GharKhataColors.TextPrimary)
                            TextButton(onClick = {
                                inputStaffName = staffName
                                inputSalary = baseSalary.toString()
                                showAddStaffDialog = true
                            }) {
                                Text("Edit", fontSize = 12.sp, color = GharKhataColors.BrandTerracotta)
                            }
                        }
                        Text(text = "Tai Monthly Salary: ${AutoCalculators.formatInr(baseSalary)}", fontSize = 13.sp, color = GharKhataColors.TextSecondary)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = GharKhataColors.BorderLight)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Chhutti (Leaves): $absentDays din", fontSize = 13.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { if (absentDays > 0) absentDays-- },
                                    modifier = Modifier.size(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) { Text("-") }
                                Button(
                                    onClick = { absentDays++ },
                                    modifier = Modifier.size(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) { Text("+") }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Advance Diya: ${AutoCalculators.formatInr(advanceTaken)}", fontSize = 13.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (advanceTaken > 0) {
                                    OutlinedButton(
                                        onClick = {
                                            advanceTaken = 0L
                                            dao?.let { d ->
                                                val existing = activeStaffList.firstOrNull()
                                                if (existing != null) {
                                                    coroutineScope.launch(Dispatchers.IO) {
                                                        try {
                                                            d.insertStaff(existing.copy(advanceBalanceInr = 0L))
                                                        } catch (_: Exception) {}
                                                    }
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) { Text("Clear", fontSize = 11.sp) }
                                }
                                Button(
                                    onClick = {
                                        advanceTaken += 500L
                                        dao?.let { d ->
                                            val sId = activeStaffList.firstOrNull()?.id ?: 0L
                                            if (sId > 0L) {
                                                coroutineScope.launch(Dispatchers.IO) {
                                                    try {
                                                        d.addAdvance(sId, 500L)
                                                    } catch (_: Exception) {}
                                                }
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) { Text("+₹500", fontSize = 12.sp) }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = GharKhataColors.BorderLight)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "1st Ko Dena Hai:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = AutoCalculators.formatInr(netPayable), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GharKhataColors.IncomeGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        val slip = AutoCalculators.generateStaffSalarySlip(staffName, monthName, baseSalary, absentDays, advanceTaken, netPayable)
                        onSendWhatsApp(slip)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GharKhataColors.IncomeGreen)
                ) {
                    Text(text = "WhatsApp Salary Slip Bhejein", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
