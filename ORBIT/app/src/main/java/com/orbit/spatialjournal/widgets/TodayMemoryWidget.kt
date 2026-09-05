package com.orbit.spatialjournal.widgets

import android.content.Context
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.orbit.spatialjournal.di.WidgetDataEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

/**
 * Shows the most recent Memory (or a "next trip" / favorite place summary) right on the
 * home screen. Reads through a Hilt EntryPoint since widgets are not injectable Activities.
 */
class TodayMemoryWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(context, WidgetDataEntryPoint::class.java)
        val memoryRepository = entryPoint.memoryRepository()
        val recent = memoryRepository.observeRecent(1).first()

        provideContent {
            Column(modifier = GlanceModifier.fillMaxSize().padding(12.dp)) {
                Text("ORBIT", style = TextStyle(fontSize = 11.sp))
                Spacer(modifier = GlanceModifier.height(4.dp))
                if (recent.isEmpty()) {
                    Text("No memories yet")
                } else {
                    val memory = recent.first()
                    Text(memory.title, style = TextStyle(fontSize = 16.sp))
                    memory.placeName?.let { Text(it) }
                }
            }
        }
    }
}

class TodayMemoryWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodayMemoryWidget()
}
