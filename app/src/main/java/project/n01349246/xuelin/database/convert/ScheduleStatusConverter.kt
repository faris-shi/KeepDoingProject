package project.n01349246.xuelin.database.convert

import androidx.room.TypeConverter
import project.n01349246.xuelin.model.ScheduleStatus

class ScheduleStatusConverter {

    @TypeConverter
    fun fromString(value: String): ScheduleStatus {
        return ScheduleStatus.valueOf(value)
    }

    @TypeConverter
    fun enumToString(status: ScheduleStatus): String {
        return status.name
    }
}