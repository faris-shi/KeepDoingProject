package project.n01349246.xuelin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import project.n01349246.xuelin.util.MiscUtil

class SplashScreenActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 3000 // 1 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                MiscUtil.registerAlarmManager(this)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, SPLASH_TIME_OUT)
        }
    }
}