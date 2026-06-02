package com.daily.app.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.daily.app.data.preferences.UserPreferences
import com.daily.app.domain.model.CheckinSource
import com.daily.app.domain.usecase.CheckinParams
import com.daily.app.domain.usecase.ICheckinUseCase
import com.daily.app.util.Result

/**
 * WorkManager worker that performs an automatic check-in when the user unlocks their device.
 *
 * Injected via Hilt's `@HiltWorker` support (Hilt 1.0.0+). The worker:
 * 1. Reads the current user's device fingerprint from [UserPreferences].
 * 2. Calls [ICheckinUseCase] with [CheckinSource.AUTO_UNLOCK].
 * 3. Returns [Result.success] on success, [Result.retry] on transient failure,
 *    or [Result.failure] on permanent errors.
 *
 * This worker is enqueued by [com.daily.app.receiver.UnlockReceiver] via
 * a OneTimeWorkRequest with REPLACE policy to avoid duplicates.
 */
@HiltWorker
class CheckinWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val checkinUseCase: ICheckinUseCase,
    private val userPreferences: UserPreferences,
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val TAG = "CheckinWorker"
        private const val MAX_RETRY_ATTEMPTS = 3
    }

    override suspend fun doWork(): Result {
        return try {
            val deviceId = userPreferences.getDeviceFingerprint().toString()
            if (deviceId.isBlank()) {
                Log.w(TAG, "Device fingerprint not set — this should not happen after onboarding")
                return Result.failure()
            }

            Log.d(TAG, "Executing auto check-in, deviceId=$deviceId, runAttempt=${runAttemptCount}")

            val checkinParams = CheckinParams(
                userId = "",       // Empty — UseCase resolves from stored JWT token
                latitude = null,
                longitude = null,
                source = CheckinSource.AUTO_UNLOCK,
                deviceId = deviceId,
            )

            when (val result = checkinUseCase(checkinParams)) {
                is Result.Success -> {
                    Log.d(TAG, "Check-in successful: checkinId=${result.data.checkinId}")
                    Result.success()
                }

                is Result.Error -> {
                    val isTransient = result.throwable is java.net.ConnectException
                            || result.throwable is java.net.SocketTimeoutException
                            || result.throwable.message?.contains("401") == true
                    if (isTransient && runAttemptCount < MAX_RETRY_ATTEMPTS) {
                        Log.w(TAG, "Transient failure (attempt $runAttemptCount): ${result.message}")
                        Result.retry()
                    } else {
                        Log.e(TAG, "Check-in failed (attempt $runAttemptCount): ${result.message}")
                        Result.failure()
                    }
                }

                is Result.Loading -> {
                    Log.w(TAG, "UseCase returned Loading state unexpectedly")
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in CheckinWorker", e)
            Result.retry()
        }
    }
}
