package project.n01349246.xuelin.database.convert

import androidx.room.TypeConverter
import project.n01349246.xuelin.model.ReminderStatus

class ReminderStatusConverter {

    @TypeConverter
    fun fromString(value: String): ReminderStatus {
        return ReminderStatus.valueOf(value)
    }

    @TypeConverter
    fun enumToString(status: ReminderStatus): String {
        return status.name
    }
}