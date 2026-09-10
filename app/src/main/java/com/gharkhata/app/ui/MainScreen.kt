package com.gharkhata.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gharkhata.app.core.designsystem.GharKhataColors

enum class AppTab(val title: String, val icon: String) {
    TODAY("Aaj Ka Hisaab", "🏠"),
    SERVICES("Ghar Ke Kaam", "🥛"),
    SAVINGS("Bachat", "💰"),
    SETTINGS("Settings", "⚙️")
}

@Composable
fun MainScreen(
    onSendWhatsApp: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(AppTab.TODAY) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = GharKhataColors.SurfaceCard,
                tonalElevation = 0.dp
            ) {
                AppTab.entries.forEach { tab ->
                    val isSelected = tab == selectedTab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = { Text(text = tab.icon, fontSize = 20.sp) },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) GharKhataColors.BrandTerracotta else GharKhataColors.TextSecondary
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GharKhataColors.BrandTerracotta,
                            selectedTextColor = GharKhataColors.BrandTerracotta,
                            indicatorColor = GharKhataColors.BrandTerracottaLight,
                            unselectedIconColor = GharKhataColors.TextSecondary,
                            unselectedTextColor = GharKhataColors.TextSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            AppTab.TODAY -> HomeScreen(
                modifier = Modifier.padding(innerPadding)
            )
            AppTab.SERVICES -> ServicesScreen(
                modifier = Modifier.padding(innerPadding),
                onSendWhatsApp = onSendWhatsApp
            )
            AppTab.SAVINGS -> SavingsScreen(
                modifier = Modifier.padding(innerPadding)
            )
            AppTab.SETTINGS -> SettingsScreen(
                modifier = Modifier.padding(innerPadding),
                onSendWhatsAppBackup = {
                    onSendWhatsApp("📦 *GharKhata Backup*\nBackup generated on ${java.time.LocalDate.now()}\nVisit https://github.com/pronextlabs/gharkhata")
                }
            )
        }
    }
}
