package com.daily.app.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.daily.app.domain.usecase.ISyncCheckinsUseCase
import com.daily.app.domain.usecase.SyncCheckinsParams
import com.daily.app.domain.util.Result

/**
 * WorkManager worker that syncs locally stored (pending) checkins with the server.
 *
 * This worker is intended to run periodically when the device has network connectivity,
 * uploading any checkins that were stored locally but could not be synced immediately
 * (e.g., due to offline mode or network loss).
 *
 * Injected via Hilt's `@HiltWorker` support. The sync logic is delegated entirely to
 * [ISyncCheckinsUseCase], which handles:
 * - Reading pending checkins from Room local DB
 * - Uploading them via Retrofit
 * - Removing successfully synced records
 *
 * Scheduling (periodic, network-required) should be configured when enqueuing this
 * worker, typically in [com.daily.app.receiver.UnlockReceiver] or during app initialization.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val syncCheckinsUseCase: ISyncCheckinsUseCase,
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val TAG = "SyncWorker"
        private const val MAX_RETRY_ATTEMPTS = 5
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Executing check-in sync, runAttempt=$runAttemptCount")

            when (val result = syncCheckinsUseCase.execute(SyncCheckinsParams(userId = ""))) {
                is Result.Success -> {
                    Log.d(TAG, "Sync completed: ${result.data} checkins uploaded")
                    Result.success()
                }

                is Result.Error -> {
                    val isTransient = result.throwable is java.net.ConnectException
                            || result.throwable is java.net.SocketTimeoutException
                            || result.throwable.message?.contains("5") == true // 5xx server errors
                    if (isTransient && runAttemptCount < MAX_RETRY_ATTEMPTS) {
                        Log.w(TAG, "Transient sync failure (attempt $runAttemptCount): ${result.message}")
                        Result.retry()
                    } else {
                        Log.e(TAG, "Sync failed permanently (attempt $runAttemptCount): ${result.message}")
                        Result.failure()
                    }
                }

                is Result.Loading -> {
                    Log.w(TAG, "UseCase returned Loading state unexpectedly")
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in SyncWorker", e)
            Result.retry()
        }
    }
}
