package com.gharkhata.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gharkhata.app.core.designsystem.GharKhataTheme
import com.gharkhata.app.ui.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GharKhataTheme {
                MainScreen(
                    onSendWhatsApp = { message ->
                        shareToWhatsApp(message)
                    }
                )
            }
        }
    }

    private fun shareToWhatsApp(message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            setPackage("com.whatsapp")
        }
        try {
            startActivity(intent)
        } catch (_: Exception) {
            // Fallback to generic share sheet if WhatsApp package isn't directly resolvable
            val chooser = Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                },
                "Hisaab Bhejein"
            )
            startActivity(chooser)
        }
    }
}
