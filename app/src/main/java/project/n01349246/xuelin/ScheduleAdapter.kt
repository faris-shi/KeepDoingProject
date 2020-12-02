package project.n01349246.xuelin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row.view.*
import org.joda.time.LocalDateTime

import project.n01349246.xuelin.model.Schedule
import project.n01349246.xuelin.model.ScheduleStatus
import project.n01349246.xuelin.util.MiscUtil


class ScheduleAdapter(
    private val context: Context,
    private val data: ArrayList<Schedule>,
    private val pref: SharedPreferences
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = data[position]
        holder.bindItem(schedule, pref, context)
        holder.itemView.setOnClickListener {
            if (schedule.status == ScheduleStatus.TODO) {
                val intent = Intent(context, ScheduleDetailActivity::class.java)
                intent.putExtra("ID", schedule.id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var pref: SharedPreferences

        private lateinit var context: Context

        fun bindItem(schedule: Schedule, pref: SharedPreferences, context: Context) {
            view.title.text = schedule.title
            this.pref = pref
            this.context = context
            settingDateRows(schedule)
        }

        private fun settingDateRows(schedule: Schedule) {
            val status: ScheduleStatus = schedule.status
            val dueDate = schedule.dueDate

            val days = MiscUtil.daysBetweenNow(dueDate)

            val text =
                when (status) {
                    ScheduleStatus.TODO -> "Due"
                    ScheduleStatus.DONE -> "Done"
                    ScheduleStatus.OVERDUE -> "Overdue"
                    ScheduleStatus.DISMISSED -> "Dismissed"
                }

            view.restDay.setBackgroundColor(getBackgroundColor(status, days))
            view.restDay.text = "$text ${formatDate(dueDate, days)}"
        }

        private fun getBackgroundColor(status: ScheduleStatus, days: Int): Int {
            return when {
                status == ScheduleStatus.DONE -> ContextCompat.getColor(context, R.color.blue)
                status == ScheduleStatus.OVERDUE -> ContextCompat.getColor(context, R.color.red)
                days <= 2 -> Color.parseColor(pref.getString("HIGH_LEVEL", "#C82013"))
                days in 3..7 -> Color.parseColor(pref.getString("MEDIUM_LEVEL", "#F69505"))
                days in 8..29 -> Color.parseColor(pref.getString("LOW_LEVEL", "#1D8A9C"))
                else -> ContextCompat.getColor(context, R.color.blue)
            }
        }

        private fun formatDate(dueDate: LocalDateTime, days: Int): String {
            val format = if (days == -1) {
                "Yesterday"
            } else if (days == 0) {
                "Today"
            } else if (days == 1) {
                "Tomorrow"
            } else if (days > 0 && days > 15) {
                "in $days days"
            } else {
                dueDate.toString(MiscUtil.DATE_FORMATTER)
            }
            return "$format at ${dueDate.toString(MiscUtil.TIME_FORMATTER)}"
        }
    }
}