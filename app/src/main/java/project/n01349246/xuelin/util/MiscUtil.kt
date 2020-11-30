package project.n01349246.xuelin.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import org.joda.time.*
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import project.n01349246.xuelin.AlarmReceiver

class MiscUtil {

    companion object {

        val DATETIME_FORMATTER: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm")

        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

        val TIME_FORMATTER: DateTimeFormatter = DateTimeFormat.forPattern("HH:mm")

        fun daysBetweenNow(datetime: LocalDateTime): Int {
            return Days.daysBetween(LocalDate.now(), datetime.toLocalDate()).days
        }

        fun registerAlarmManager(context: Context) {
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(context, 0, intent, 0)
            }
            alarmMgr?.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 1 * 1000,
                1000 * 60,
                alarmIntent
            )

            val receiver = ComponentName(context, AlarmReceiver::class.java)

            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }
}