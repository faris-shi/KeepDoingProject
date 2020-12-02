package project.n01349246.xuelin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import project.n01349246.xuelin.database.ScheduleDatabase
import project.n01349246.xuelin.databinding.ActivityMainBinding
import project.n01349246.xuelin.model.Schedule
import project.n01349246.xuelin.model.ScheduleStatus
import project.n01349246.xuelin.util.MiscUtil
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private var data = ArrayList<Schedule>()

    private var keyword: String = ""

    private var status: ScheduleStatus = ScheduleStatus.TODO

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: ScheduleAdapter

    private lateinit var database: ScheduleDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        database = ScheduleDatabase.getInstance(applicationContext)
        this.setTitle(R.string.activity_main_title)

        val pref = getSharedPreferences("keep_doing", Context.MODE_PRIVATE)
        adapter = ScheduleAdapter(this, data, pref)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                keyword = newText
                displaySchedules()
                return false
            }
        })

        binding.tabL.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> status = ScheduleStatus.TODO
                    1 -> status = ScheduleStatus.OVERDUE
                    2 -> status = ScheduleStatus.DONE
                }
                displaySchedules()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, ScheduleDetailActivity::class.java)
            startActivity(intent)
        }

        binding.freshLayout?.setOnRefreshListener {
            displaySchedules()
        }


        displaySchedules()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.change_color -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.help -> {
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displaySchedules() {
        doAsync {
            data.clear()
            data.addAll(
                database.scheduleDAO()
                    .findByCriteria(keyword, status, status == ScheduleStatus.TODO)
            )
            uiThread {
                adapter.notifyDataSetChanged()
                binding.freshLayout?.isRefreshing = false
            }
        }
    }
}