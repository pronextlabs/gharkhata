package com.gharkhata.app.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = GharKhataColors.BrandTerracotta,
    onPrimary = GharKhataColors.SurfaceCard,
    primaryContainer = GharKhataColors.BrandTerracottaLight,
    onPrimaryContainer = GharKhataColors.BrandTerracotta,
    background = GharKhataColors.CanvasBone,
    onBackground = GharKhataColors.TextPrimary,
    surface = GharKhataColors.SurfaceCard,
    onSurface = GharKhataColors.TextPrimary,
    outline = GharKhataColors.BorderLight
)

@Composable
fun GharKhataTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
