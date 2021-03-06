package project.n01349246.xuelin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import project.n01349246.xuelin.database.ScheduleDatabase
import project.n01349246.xuelin.databinding.ActivityScheduleDetailBinding
import project.n01349246.xuelin.model.ReminderStatus
import project.n01349246.xuelin.model.ReminderUnit
import project.n01349246.xuelin.model.Schedule
import project.n01349246.xuelin.model.ScheduleStatus
import project.n01349246.xuelin.util.MiscUtil

class ScheduleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScheduleDetailBinding

    private lateinit var database: ScheduleDatabase

    private var id: Long? = null

    private var status: ScheduleStatus = ScheduleStatus.TODO

    private var reminderUnit = ReminderUnit.HOUR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_detail)
        database = ScheduleDatabase.getInstance(applicationContext)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id = intent.getLongExtra("ID", 0L)

        Log.i("Faris", "id: $id")

        if (id == 0L) {
            binding.updateBtns.visibility = View.GONE
            this.title = "Add Schedule"
        } else {
            binding.btnAdd.visibility = View.GONE
            this.title = "Modify Schedule"
        }

        if (id != 0L) {
            fillUp(savedInstanceState)
        }

        binding.btnAdd.setOnClickListener {
            if (checkValue()) {
                val intent = Intent(this, MainActivity::class.java)
                doAsync {
                    val schedule = generateSchedule()
                    database.scheduleDAO().insertOrUpdate(schedule)

                    uiThread {
                        toast("Added!")
                        startActivity(intent)
                    }
                }
            }
        }

        binding.btnUpdate.setOnClickListener {
            if (checkValue()) {
                val intent = Intent(this, MainActivity::class.java)

                doAsync {
                    val schedule = generateSchedule()
                    database.scheduleDAO().insertOrUpdate(schedule)
                    uiThread {
                        toast("Updated!")
                        startActivity(intent)
                    }
                }
            }
        }

        binding.btnDismiss.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            doAsync {

                database.scheduleDAO().updateStatus(id!!, ScheduleStatus.DISMISSED)
                uiThread {
                    toast("Dismissed!")
                    startActivity(intent)
                }
            }
        }

        binding.btnDone.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            doAsync {
                database.scheduleDAO().updateStatus(id!!, ScheduleStatus.DONE)
                uiThread {
                    toast("Done!")
                    startActivity(intent)
                }
            }
        }

        binding.timePicker.setOnClickListener {
            openTimePickupDialog()
        }

        binding.datePicker.setOnClickListener {
            openDatePickupDialog()
        }

        binding.dueTime.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                hideKeyboard(binding.dueTime)
                openTimePickupDialog()
                view.clearFocus()
            }
        }

        binding.dueDate.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                hideKeyboard(binding.dueDate)
                openDatePickupDialog()
                view.clearFocus()
            }
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            reminderUnit = when (checkedId) {
                R.id.minutes -> ReminderUnit.MINUTE
                R.id.hours -> ReminderUnit.HOUR
                R.id.days -> ReminderUnit.DAY
                else -> ReminderUnit.WEEK
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putCharSequence("title", binding.title.text)
        outState.putCharSequence("dueDate", binding.dueDate.text)
        outState.putCharSequence("dueTime", binding.dueTime.text)
        outState.putCharSequence("remindNum", binding.remindNum.text)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        binding.title.setText(savedInstanceState.getCharSequence("title"))
        binding.dueDate.setText(savedInstanceState.getCharSequence("dueDate"))
        binding.dueTime.setText(savedInstanceState.getCharSequence("dueTime"))
        binding.remindNum.setText(savedInstanceState.getCharSequence("remindNum"))

        when (reminderUnit) {
            ReminderUnit.MINUTE -> binding.minutes.isChecked = true
            ReminderUnit.HOUR -> binding.hours.isChecked = true
            ReminderUnit.DAY -> binding.days.isChecked = true
            ReminderUnit.WEEK -> binding.weeks.isChecked = true
        }

        if (status == ScheduleStatus.DONE || status == ScheduleStatus.OVERDUE) {
            binding.updateBtns.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }


    private fun checkValue(): Boolean {

        binding.title.error = null
        if (binding.title.text.isBlank()) {
            binding.title.error = "title required!"
            binding.title.requestFocus()
            return false
        }

        binding.dueDate.error = null
        if (binding.dueDate.text.isBlank()) {
            binding.dueDate.error = "due date required!"
            binding.dueDate.requestFocus()
            return false
        }

        binding.dueTime.error = null
        if (binding.dueTime.text.isBlank()) {
            binding.dueTime.error = "due time required!"
            binding.dueTime.requestFocus()
            return false
        }

        binding.dueDate.error = null
        val date = LocalDate.parse(binding.dueDate.text.toString(), MiscUtil.DATE_FORMATTER)
        if (LocalDate.now().isAfter(date)) {
            binding.dueDate.error = "due date must be greater than now"
            binding.dueDate.requestFocus()
            return false
        }

        binding.remindNum.error = null
        if (binding.remindNum.text.isBlank()) {
            binding.remindNum.error = "reminder required!"
            binding.remindNum.requestFocus()
            return false
        }
        return true
    }

    private fun generateSchedule(): Schedule {
        val dueDate = LocalDateTime.parse(
            "${binding.dueDate.text} ${binding.dueTime.text}",
            MiscUtil.DATETIME_FORMATTER
        )
        val remindNum = binding.remindNum.text.toString().toInt()

        val remindDate = when (reminderUnit) {
            ReminderUnit.MINUTE -> dueDate.minusMinutes(remindNum)
            ReminderUnit.HOUR -> dueDate.minusHours(remindNum)
            ReminderUnit.DAY -> dueDate.minusDays(remindNum)
            ReminderUnit.WEEK -> dueDate.minusWeeks(remindNum)
        }

        return Schedule(
            if (id == 0L) null else id,
            binding.title.text.toString(),
            dueDate,
            remindDate,
            remindNum,
            reminderUnit,
            ReminderStatus.NONE,
            ScheduleStatus.TODO
        )
    }


    private fun fillUp(bundle: Bundle?) {
        if (bundle?.getCharSequence("title") != null) {
            return
        }
        doAsync {
            val schedule = database.scheduleDAO().findById(id!!)
            uiThread {
                binding.title.setText(schedule.title)
                binding.dueDate.setText(schedule.dueDate.toLocalDate().toString("yyyy-MM-dd"))
                binding.dueTime.setText(schedule.dueDate.toLocalTime().toString("HH:mm"))
                binding.remindNum.setText(schedule.reminderNum.toString())

                reminderUnit = schedule.reminderUnit
                when (reminderUnit) {
                    ReminderUnit.MINUTE -> binding.minutes.isChecked = true
                    ReminderUnit.HOUR -> binding.hours.isChecked = true
                    ReminderUnit.DAY -> binding.days.isChecked = true
                    ReminderUnit.WEEK -> binding.weeks.isChecked = true
                }

                status = schedule.status
            }
        }
    }

    private fun openTimePickupDialog() {
        val listener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            val time = LocalTime(hour, minute)
            binding.dueTime.setText(time.toString(MiscUtil.TIME_FORMATTER))
        }

        val datetime = DateTime.now()
        TimePickerDialog(
            this,
            listener,
            datetime.hourOfDay,
            datetime.minuteOfHour,
            true
        ).show()
    }

    private fun openDatePickupDialog() {
        val listener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val date = LocalDate(year, monthOfYear + 1, dayOfMonth)
            binding.dueDate.setText(date.toString(MiscUtil.DATE_FORMATTER))
        }

        val datetime = DateTime.now()
        DatePickerDialog(
            this,
            listener,
            datetime.year,
            datetime.monthOfYear - 1,
            datetime.dayOfMonth
        ).show()
    }

    private fun hideKeyboard(editText: EditText) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }


}