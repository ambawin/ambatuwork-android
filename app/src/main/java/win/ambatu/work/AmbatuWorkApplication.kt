package win.ambatu.work

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AmbatuWorkApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "project_invitations",
            "Project Invitations",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for project invitation and collaboration events"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
