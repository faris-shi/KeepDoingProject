package project.n01349246.xuelin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import project.n01349246.xuelin.databinding.ActivityAboutBinding
import project.n01349246.xuelin.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_help)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.title = "Help"
        binding.webView.loadUrl("file:///android_asset/help.html")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}