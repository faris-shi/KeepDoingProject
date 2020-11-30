package project.n01349246.xuelin.database.convert

import androidx.room.TypeConverter
import project.n01349246.xuelin.model.ReminderUnit

class ReminderUnitConverter {

    @TypeConverter
    fun fromString(value: String): ReminderUnit {
        return ReminderUnit.valueOf(value)
    }

    @TypeConverter
    fun enumToString(unit: ReminderUnit): String {
        return unit.name
    }
}