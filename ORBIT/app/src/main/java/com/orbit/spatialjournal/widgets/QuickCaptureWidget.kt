package com.orbit.spatialjournal.widgets

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import com.orbit.spatialjournal.MainActivity
import android.content.Intent
import android.net.Uri

/**
 * Home-screen Quick Capture widget: one tap opens Capture with location + timestamp
 * pre-filled automatically (see CaptureViewModel.captureCurrentContext()).
 */
class QuickCaptureWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Row(
                modifier = GlanceModifier.fillMaxWidth().background(ColorProvider(androidx.compose.ui.graphics.Color(0xFF1F6FEB))).padding(12.dp)
            ) {
                WidgetCaptureButton(context, "orbit://capture?type=photo", "📷")
                Spacer(modifier = GlanceModifier.width(8.dp))
                WidgetCaptureButton(context, "orbit://capture?type=voice", "🎙")
                Spacer(modifier = GlanceModifier.width(8.dp))
                WidgetCaptureButton(context, "orbit://capture?type=note", "📝")
            }
        }
    }

    @androidx.compose.runtime.Composable
    private fun WidgetCaptureButton(context: Context, deepLink: String, emoji: String) {
        Box(
            modifier = GlanceModifier
                .background(ColorProvider(androidx.compose.ui.graphics.Color.White))
                .cornerRadius(12.dp)
                .padding(10.dp)
                .clickable(
                    actionStartActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(deepLink), context, MainActivity::class.java)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji)
        }
    }
}

class QuickCaptureWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QuickCaptureWidget()
}
