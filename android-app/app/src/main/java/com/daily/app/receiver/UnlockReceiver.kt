package com.daily.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.daily.app.work.CheckinWorker
import com.daily.app.work.SyncWorker
import java.util.concurrent.TimeUnit

/**
 * BroadcastReceiver that listens for device unlock and boot completed events.
 *
 * - [ACTION_USER_PRESENT]: User unlocked the device — triggers an auto check-in
 *   via CheckinWorker through WorkManager (survives Doze mode and process death).
 * - [ACTION_BOOT_COMPLETED]: Device booted — enqueues a pending check-in sync
 *   so any locally stored checkins get uploaded when network is available.
 *
 * Uses WorkManager's enqueueUniqueWork with REPLACE policy to prevent duplicate
 * triggers. A 500ms initial delay gives the system time to settle after unlock,
 * avoiding race conditions with rapid lock/unlock cycles.
 */
class UnlockReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "UnlockReceiver"
        private const val CHECKIN_UNIQUE_WORK_NAME = "auto_checkin"
        private const val SYNC_UNIQUE_WORK_NAME = "boot_sync"
        /** Small delay after unlock before enqueuing check-in, prevents duplicate triggers. */
        private const val CHECKIN_DELAY_MS = 500L
        private const val SYNC_DELAY_MS = 2000L
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_USER_PRESENT -> {
                Log.d(TAG, "User unlocked device — enqueuing auto check-in")
                enqueueCheckinWork(context)
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "Boot completed — enqueuing pending check-in sync")
                enqueueSyncWork(context)
            }
        }
    }

    /**
     * Enqueues a one-time check-in work request.
     *
     * Uses [enqueueUniqueWork] with REPLACE to ensure only one check-in is pending at a time.
     * If the user unlocks rapidly, the previous pending work is cancelled and replaced,
     * preventing duplicate check-in records.
     */
    private fun enqueueCheckinWork(context: Context) {
        val constraints = Constraints.Builder()
            .build() // No constraints — check-in works offline (stored locally)

        val checkinWork = OneTimeWorkRequestBuilder<CheckinWorker>()
            .setConstraints(constraints)
            .setInitialDelay(CHECKIN_DELAY_MS, TimeUnit.MILLISECONDS)
            .addTag("checkin")
            .build()

        WorkManager.getInstance(context).apply {
            // Cancel any existing check-in work to avoid duplicates
            cancelUniqueWork(CHECKIN_UNIQUE_WORK_NAME)
            // Enqueue with REPLACE: if another work is enqueued before this one runs,
            // it will be cancelled and replaced by the newer one.
            enqueueUniqueWork(
                CHECKIN_UNIQUE_WORK_NAME,
                androidx.work.ExistingWorkPolicy.REPLACE,
                checkinWork,
            )
        }
    }

    /**
     * Enqueues a one-time sync work request after boot.
     *
     * After device reboot, any locally cached checkins (that couldn't sync because the
     * app/service was stopped) should be uploaded. This runs with network constraints
     * to ensure we only sync when connected.
     */
    private fun enqueueSyncWork(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
            .build()

        val syncWork = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setInitialDelay(SYNC_DELAY_MS, TimeUnit.MILLISECONDS)
            .addTag("sync")
            .build()

        WorkManager.getInstance(context).apply {
            cancelUniqueWork(SYNC_UNIQUE_WORK_NAME)
            enqueueUniqueWork(
                SYNC_UNIQUE_WORK_NAME,
                androidx.work.ExistingWorkPolicy.REPLACE,
                syncWork,
            )
        }
    }
}
