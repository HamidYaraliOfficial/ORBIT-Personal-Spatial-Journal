package com.orbit.spatialjournal.location

import android.content.Context
import android.os.BatteryManager
import android.os.PowerManager
import com.orbit.spatialjournal.core.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Decides how often ORBIT is allowed to sample location for Smart Context / Visit Detection.
 * The goal is that a user who leaves Location on "Smart Context" never notices a battery hit:
 * intervals widen automatically under Battery Saver or low charge, and Doze-friendly WorkManager
 * jobs (not a sticky foreground poll loop) do the actual sampling.
 */
@Singleton
class BatteryAwareLocationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val powerManager by lazy { context.getSystemService(Context.POWER_SERVICE) as PowerManager }
    private val batteryManager by lazy { context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager }

    fun isBatterySaverOn(): Boolean = powerManager.isPowerSaveMode

    fun isBatteryLow(): Boolean {
        val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return level in 0..15
    }

    /** Recommended sampling interval in milliseconds, given current device power state. */
    fun currentIntervalMillis(): Long = when {
        isBatterySaverOn() || isBatteryLow() -> Constants.LOCATION_INTERVAL_SAVER_MS
        else -> Constants.LOCATION_INTERVAL_NORMAL_MS
    }
}
