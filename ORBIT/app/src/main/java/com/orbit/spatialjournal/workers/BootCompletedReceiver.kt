package com.orbit.spatialjournal.workers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.orbit.spatialjournal.core.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

/** Re-schedules periodic WorkManager jobs after a reboot (WorkManager itself survives reboot
 * for jobs already enqueued, but we re-verify here in case the user cleared app data). */
@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val workManager = WorkManager.getInstance(context)

        workManager.enqueueUniquePeriodicWork(
            Constants.WORK_REMINDER_CHECK, ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<ReminderCheckWorker>(15, TimeUnit.MINUTES).build()
        )
    }
}
