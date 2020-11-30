package project.n01349246.xuelin.database.convert

import androidx.room.TypeConverter
import org.joda.time.DateTime
import org.joda.time.LocalDateTime


class LocalDateTimeConverter {

    @TypeConverter
    fun fromTimestamp(value: Long): LocalDateTime {
        return DateTime(value).toLocalDateTime()
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime): Long {
        return date.toDateTime().toInstant().millis
    }
}