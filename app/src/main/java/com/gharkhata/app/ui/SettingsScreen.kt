package com.gharkhata.app.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gharkhata.app.R
import com.gharkhata.app.core.designsystem.GharKhataColors
import com.gharkhata.app.core.designsystem.tactileClick

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onSendWhatsAppBackup: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf("Hinglish") }

    val languages = listOf("Hinglish", "हिन्दी", "English", "मराठी", "தமிழ்", "తెలుగు", "ગુજરાતી")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GharKhataColors.CanvasBone)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- 1. Prominent Credits to pronextlabs ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GharKhataColors.SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GharKhataColors.BorderLight))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "GharKhata App Icon",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "GharKhata (घरखाता)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GharKhataColors.BrandTerracotta
                    )
                    Text(
                        text = "Version 1.1.0 (Production Release)",
                        fontSize = 12.sp,
                        color = GharKhataColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Credits Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = GharKhataColors.BrandTerracottaLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GharKhataColors.BrandTerracotta.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Crafted with ❤️ by pronextlabs",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GharKhataColors.BrandTerracotta,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "100% Free & Open-Source Public Good for Indian Homemakers.",
                        fontSize = 11.sp,
                        color = GharKhataColors.TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/pronextlabs/gharkhata"))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("GitHub Repo", fontSize = 12.sp, color = GharKhataColors.BrandTerracotta)
                        }
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/pronextlabs"))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("pronextlabs", fontSize = 12.sp, color = GharKhataColors.TextPrimary)
                        }
                    }
                }
            }
        }

        // --- 2. Privacy & Offline Guarantee ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GharKhataColors.IncomeGreenLight),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GharKhataColors.IncomeGreen.copy(alpha = 0.3f)))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "🛡️", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "100% Offline & Private",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GharKhataColors.IncomeGreen
                        )
                        Text(
                            text = "Zero tracking. Zero ads. Aapka hisaab sirf aapke device par rehta hai.",
                            fontSize = 11.sp,
                            color = GharKhataColors.IncomeGreen.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // --- 3. Language Selector ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GharKhataColors.SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GharKhataColors.BorderLight))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🌐 Bhasha Chunein (Language)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GharKhataColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        languages.take(4).forEach { lang ->
                            val isSelected = lang == selectedLanguage
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GharKhataColors.BrandTerracotta else GharKhataColors.SurfaceSubtle)
                                    .tactileClick { selectedLanguage = lang }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lang,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else GharKhataColors.TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 4. Backups & Data Export ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GharKhataColors.SurfaceCard),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GharKhataColors.BorderLight))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "💾 Data Backup & Portability",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GharKhataColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Naya phone lene par ya data safe rakhne ke liye backup save karein.",
                        fontSize = 12.sp,
                        color = GharKhataColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onSendWhatsAppBackup,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GharKhataColors.IncomeGreen)
                    ) {
                        Text("WhatsApp Par Backup Share Karein", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
