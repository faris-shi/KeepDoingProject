package project.n01349246.xuelin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.LocalDateTime
import project.n01349246.xuelin.database.ScheduleDatabase
import project.n01349246.xuelin.model.ReminderStatus
import project.n01349246.xuelin.model.ScheduleStatus
import project.n01349246.xuelin.util.MiscUtil

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            MiscUtil.registerAlarmManager(context)
        }
        doAsync {
            Log.i("Faris", "Task start")

            createNotificationChannel(context)

            val database = ScheduleDatabase.getInstance(context)
            val now = LocalDateTime.now()
            val reminderList = database.scheduleDAO().findAllNeedReminders(now)
            val overdueList = database.scheduleDAO().findAllWillOverdue(now)

            uiThread {
                reminderList.forEach {
                    val content = "${it.title}, take action please!"
                    createNotification(it.id!!, content, context)
                }

                overdueList.forEach {
                    val content = "${it.title} has been overdue!"
                    createNotification(it.id!!, content, context)
                }
            }

            reminderList.forEach {
                database.scheduleDAO().updateReminderStatus(it.id!!, ReminderStatus.DONE)
            }

            overdueList.forEach {
                database.scheduleDAO().updateStatus(it.id!!, ScheduleStatus.OVERDUE)
            }
        }
    }


    private fun createNotification(id: Long, content: String, context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val builder = NotificationCompat.Builder(context, "KEEP_DOING")
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("Schedule Notification")
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(id.toInt(), builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("KEEP_DOING", name, importance).apply {
                description = descriptionText
            }
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}