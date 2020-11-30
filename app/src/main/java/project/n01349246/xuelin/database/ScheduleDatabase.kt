package project.n01349246.xuelin.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import project.n01349246.xuelin.database.convert.LocalDateTimeConverter
import project.n01349246.xuelin.database.convert.ReminderStatusConverter
import project.n01349246.xuelin.database.convert.ReminderUnitConverter
import project.n01349246.xuelin.database.convert.ScheduleStatusConverter
import project.n01349246.xuelin.model.Schedule

@Database(entities = [Schedule::class], version = 1)
@TypeConverters(
    LocalDateTimeConverter::class,
    ScheduleStatusConverter::class,
    ReminderUnitConverter::class,
    ReminderStatusConverter::class
)
abstract class ScheduleDatabase : RoomDatabase() {

    abstract fun scheduleDAO(): ScheduleDAO

    companion object {

        @Volatile
        private var INSTANCE: ScheduleDatabase? = null

        fun getInstance(context: Context): ScheduleDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    ScheduleDatabase::class.java,
                    "schedule.db"
                )
                    .build()
            }
            return INSTANCE as ScheduleDatabase
        }
    }
}