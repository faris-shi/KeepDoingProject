package project.n01349246.xuelin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import project.n01349246.xuelin.databinding.ActivityColorBinding

class ColorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityColorBinding

    private lateinit var pref: SharedPreferences

    private var lowLevel: String = ""

    private var mediumLevel: String = ""

    private var highLevel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_color)
        pref = getSharedPreferences("keep_doing", Context.MODE_PRIVATE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.title = "Change Color"

        lowLevel = pref.getString("LOW_LEVEL", "#1D8A9C").toString()
        when (lowLevel) {
            "#1D8A9C" -> binding.lowLevel1.isChecked = true
            "#C3D52D" -> binding.lowLevel2.isChecked = true
            "#ACBC28" -> binding.lowLevel3.isChecked = true
        }

        mediumLevel = pref.getString("MEDIUM_LEVEL", "#F69505").toString()
        when (mediumLevel) {
            "#F69505" -> binding.mediumLevel1.isChecked = true
            "#E8A94D" -> binding.mediumLevel2.isChecked = true
            "#BC7A1A" -> binding.mediumLevel3.isChecked = true
        }

        highLevel = pref.getString("HIGH_LEVEL", "#C82013").toString()
        when (highLevel) {
            "#C82013" -> binding.highLevel1.isChecked = true
            "#EA837B" -> binding.highLevel2.isChecked = true
            "#D1463B" -> binding.highLevel3.isChecked = true
        }

        binding.save.setOnClickListener {
            val editor = pref.edit()
            editor.putString("LOW_LEVEL", lowLevel)
            editor.putString("MEDIUM_LEVEL", mediumLevel)
            editor.putString("HIGH_LEVEL", highLevel)
            editor.apply()

            startActivity(Intent(this, MainActivity::class.java))
        }


        binding.lowLevel.setOnCheckedChangeListener { _, checkedId ->
            lowLevel = when (checkedId) {
                R.id.lowLevel_2 -> "#C3D52D"
                R.id.lowLevel_3 -> "#ACBC28"
                else -> "#1D8A9C"
            }
        }

        binding.mediumLevel.setOnCheckedChangeListener { _, checkedId ->
            mediumLevel = when (checkedId) {
                R.id.mediumLevel_2 -> "#E8A94D"
                R.id.mediumLevel_3 -> "#BC7A1A"
                else -> "#F69505"
            }
        }


        binding.highLevel.setOnCheckedChangeListener { _, checkedId ->
            highLevel = when (checkedId) {
                R.id.highLevel_2 -> "#EA837B"
                R.id.highLevel_3 -> "#D1463B"
                else -> "#C82013"
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}