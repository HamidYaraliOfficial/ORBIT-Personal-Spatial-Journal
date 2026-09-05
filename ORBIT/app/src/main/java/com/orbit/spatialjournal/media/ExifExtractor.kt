package com.orbit.spatialjournal.media

import android.content.Context
import androidx.exifinterface.media.ExifInterface
import com.orbit.spatialjournal.core.model.ExifData
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/** Extracts capture date / GPS / camera metadata from a photo, powering the Photo Intelligence System. */
@Singleton
class ExifExtractor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val exifDateFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US)

    fun extract(uriString: String): ExifData? {
        return try {
            context.contentResolver.openInputStream(android.net.Uri.parse(uriString))?.use { stream ->
                val exif = ExifInterface(stream)
                val latLong = FloatArray(2)
                val hasLatLong = exif.getLatLong(latLong)

                val dateString = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
                    ?: exif.getAttribute(ExifInterface.TAG_DATETIME)
                val capturedAt = dateString?.let { runCatching { exifDateFormat.parse(it)?.time }.getOrNull() }

                ExifData(
                    capturedAt = capturedAt,
                    latitude = if (hasLatLong) latLong[0].toDouble() else null,
                    longitude = if (hasLatLong) latLong[1].toDouble() else null,
                    cameraMake = exif.getAttribute(ExifInterface.TAG_MAKE),
                    cameraModel = exif.getAttribute(ExifInterface.TAG_MODEL),
                    widthPx = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)?.toIntOrNull(),
                    heightPx = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)?.toIntOrNull()
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}
