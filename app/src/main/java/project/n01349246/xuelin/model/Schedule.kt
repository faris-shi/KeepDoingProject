package project.n01349246.xuelin.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.LocalDateTime

@Entity(tableName = "schedule")
data class Schedule(

    @PrimaryKey(autoGenerate = true)
    var id: Long?,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "due_date")
    var dueDate: LocalDateTime,

    @ColumnInfo(name = "reminder_date")
    var reminderDate: LocalDateTime,

    @ColumnInfo(name = "reminder_num")
    var reminderNum: Int,

    @ColumnInfo(name = "reminder_unit")
    var reminderUnit: ReminderUnit,

    @ColumnInfo(name = "reminder_status")
    var reminderStatus: ReminderStatus,

    @ColumnInfo(name = "status")
    var status: ScheduleStatus
)