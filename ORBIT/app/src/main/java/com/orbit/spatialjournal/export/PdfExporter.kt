package com.orbit.spatialjournal.export

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.orbit.spatialjournal.core.model.Memory
import com.orbit.spatialjournal.core.util.DateTimeUtils
import java.io.OutputStream
import javax.inject.Inject

/** Renders a simple, readable PDF report of memories using Android's built-in PdfDocument API. */
class PdfExporter @Inject constructor() {

    fun export(memories: List<Memory>, title: String, out: OutputStream) {
        val document = PdfDocument()
        val pageWidth = 595; val pageHeight = 842 // A4 at 72dpi
        val titlePaint = Paint().apply { textSize = 20f; isFakeBoldText = true }
        val bodyPaint = Paint().apply { textSize = 12f }
        val mutedPaint = Paint().apply { textSize = 10f; alpha = 160 }

        var pageNumber = 1
        var page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas: Canvas = page.canvas
        var y = 48f

        canvas.drawText(title, 40f, y, titlePaint); y += 32f

        for (memory in memories) {
            if (y > pageHeight - 80) {
                document.finishPage(page)
                pageNumber++
                page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                canvas = page.canvas
                y = 48f
            }
            canvas.drawText(memory.title, 40f, y, bodyPaint); y += 16f
            val meta = buildString {
                append(DateTimeUtils.formatShortDate(memory.timestamp))
                memory.placeName?.let { append(" · $it") }
                if (memory.tags.isNotEmpty()) append(" · #${memory.tags.joinToString(" #")}")
            }
            canvas.drawText(meta, 40f, y, mutedPaint); y += 20f
        }

        document.finishPage(page)
        document.writeTo(out)
        document.close()
    }
}
