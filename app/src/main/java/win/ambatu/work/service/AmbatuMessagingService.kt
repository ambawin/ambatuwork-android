package win.ambatu.work.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import win.ambatu.work.R
import win.ambatu.work.data.repository.AuthRepository
import win.ambatu.work.data.storage.SessionManager
import win.ambatu.work.feature.invitation.InvitationActivity
import javax.inject.Inject

@AndroidEntryPoint
class AmbatuMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var authRepository: AuthRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ─── FCM lifecycle ────────────────────────────────────────────────────────

    /**
     * Called whenever FCM generates a new registration token (first run or token refresh).
     * We persist it locally and immediately try to upload it to the backend if the user
     * already has a valid Sanctum session.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "New FCM token received")
        sessionManager.saveFcmToken(token)

        val sanctumToken = sessionManager.getToken() ?: return
        serviceScope.launch {
            runCatching { authRepository.updateDeviceToken(sanctumToken, token) }
                .onSuccess { Log.d(TAG, "FCM token uploaded successfully") }
                .onFailure { Log.e(TAG, "Failed to upload FCM token", it) }
        }
    }

    /**
     * Called when a data message is received (or a notification message while the app is
     * in the foreground). Builds and shows a notification; if the payload carries
     * type == "project_invitation" the tap-action opens InvitationActivity.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "Message received from: ${message.from}")

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "AmbatuWork"
        val body = message.notification?.body
            ?: message.data["body"]
            ?: ""
        val type = message.data["type"]

        createNotificationChannel()

        val tapIntent = if (type == "project_invitation" && sessionManager.getToken() != null) {
            InvitationActivity.createIntent(this).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                         android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        } else {
            packageManager.getLaunchIntentForPackage(packageName)?.apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                         android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
                message.data.forEach { (key, value) ->
                    putExtra(key, value)
                }
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(this)
                .notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission was denied — silently ignore.
            Log.w(TAG, "POST_NOTIFICATIONS permission denied, notification not shown", e)
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Project Invitations",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for project invitation and collaboration events"
        }
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        private const val TAG = "FCM"
        const val CHANNEL_ID = "project_invitations"
    }
}
