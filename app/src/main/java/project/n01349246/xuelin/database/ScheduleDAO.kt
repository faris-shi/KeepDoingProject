package project.n01349246.xuelin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.joda.time.LocalDateTime
import project.n01349246.xuelin.model.ReminderStatus
import project.n01349246.xuelin.model.Schedule
import project.n01349246.xuelin.model.ScheduleStatus

@Dao
interface ScheduleDAO {

    @Query("SELECT * FROM schedule WHERE title LIKE '%' || :keyword || '%' AND status = :status ORDER BY CASE WHEN :isAsc THEN due_date END ASC, CASE WHEN NOT :isAsc THEN due_date END DESC")
    fun findByCriteria(keyword: String?, status: ScheduleStatus, isAsc: Boolean): List<Schedule>

    @Query("SELECT * FROM schedule WHERE status = 'TODO' and reminder_status = 'NONE' and reminder_date <= :now")
    fun findAllNeedReminders(now: LocalDateTime): List<Schedule>

    @Query("SELECT * FROM schedule WHERE status = 'TODO' and due_date <= :now")
    fun findAllWillOverdue(now: LocalDateTime): List<Schedule>

    @Query("SELECT * FROM schedule WHERE id = :id")
    fun findById(id: Long): Schedule

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(schedule: Schedule)

    @Query("update schedule set status =:status WHERE id = :id")
    fun updateStatus(id: Long, status: ScheduleStatus)

    @Query("update schedule set reminder_status =:status WHERE id = :id")
    fun updateReminderStatus(id: Long, status: ReminderStatus)
}