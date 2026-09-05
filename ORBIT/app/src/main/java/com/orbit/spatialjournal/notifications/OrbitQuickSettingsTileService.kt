package com.orbit.spatialjournal.notifications

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.service.quicksettings.TileService
import com.orbit.spatialjournal.MainActivity
import dagger.hilt.android.AndroidEntryPoint

/** Quick Settings tile: one tap from the notification shade straight into Quick Capture. */
@AndroidEntryPoint
class OrbitQuickSettingsTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("orbit://capture"), this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startActivityAndCollapse(
                android.app.PendingIntent.getActivity(this, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE)
            )
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }
}
