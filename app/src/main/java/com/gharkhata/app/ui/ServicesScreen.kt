package com.gharkhata.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.gharkhata.app.domain.model.MilkBottleLog
import com.gharkhata.app.domain.model.MilkStatus
import java.time.LocalDate

@Composable
fun ServicesScreen(
    modifier: Modifier = Modifier,
    onSendWhatsApp: (String) -> Unit = {}
) {
    var activeSubTab by remember { mutableStateOf("MILK") } // MILK or STAFF

    val today = remember { LocalDate.now() }
    val daysInMonth = remember { today.lengthOfMonth() }
    val monthName = remember { today.month.name.lowercase().replaceFirstChar { it.uppercase() } }

    val milkLogs = remember {
        mutableStateListOf<MilkBottleLog>().apply {
            for (day in 1..daysInMonth) {
                add(MilkBottleLog(dayOfMonth = day, dateEpochDay = day.toLong(), status = MilkStatus.FULL_LITER))
            }
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
            Text(text = "Din Par Tap Karein (1L -> 1.5L -> Bandh)", fontSize = 12.sp, color = GharKhataColors.TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(milkLogs.size) { index ->
                    val log = milkLogs[index]
                    val isBandh = log.status == MilkStatus.NO_MILK
                    val isExtra = log.status == MilkStatus.LITER_AND_HALF || log.status == MilkStatus.TWO_LITERS

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isBandh) GharKhataColors.ExpenseRedLight
                                else if (isExtra) GharKhataColors.BrandTerracottaLight
                                else GharKhataColors.SurfaceCard
                            )
                            .border(1.dp, GharKhataColors.BorderLight, RoundedCornerShape(8.dp))
                            .tactileClick {
                                milkLogs[index] = log.copy(status = log.status.next())
                            }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "${log.dayOfMonth}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GharKhataColors.TextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBandh) "❌" else "🥛",
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (isBandh) "0L" else log.status.label.replace(" L", "L"),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isBandh) GharKhataColors.ExpenseRed else GharKhataColors.TextPrimary
                        )
                    }
                }
            }

            // --- WhatsApp Button ---
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val msg = AutoCalculators.generateMilkWhatsAppSlip(monthName, totalLiters, 66, totalMilkBill)
                    onSendWhatsApp(msg)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GharKhataColors.IncomeGreen)
            ) {
                Text(text = "WhatsApp Par Doodhwale Ko Bhejein", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        } else {
            // --- Staff Screen Content ---
            var baseSalary by remember { mutableStateOf(4000L) }
            var absentDays by remember { mutableStateOf(2) }
            var advanceTaken by remember { mutableStateOf(1000L) }

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
                    Text(text = "Sunita Bai (Jhadu Pocha)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GharKhataColors.TextPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Tai Monthly Salary: ${AutoCalculators.formatInr(baseSalary)}", fontSize = 13.sp, color = GharKhataColors.TextSecondary)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = GharKhataColors.BorderLight)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
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
                        Button(
                            onClick = { advanceTaken += 500L },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) { Text("+₹500", fontSize = 12.sp) }
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
                    val slip = AutoCalculators.generateStaffSalarySlip("Sunita Bai", monthName, baseSalary, absentDays, advanceTaken, netPayable)
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
