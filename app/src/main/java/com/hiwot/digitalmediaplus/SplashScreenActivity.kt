package com.hiwot.digitalmediaplus

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.WindowCompat
import com.hiwot.digitalmediaplus.databinding.ActivitySplashScreenBinding

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.run{
            WindowCompat.setDecorFitsSystemWindows(this, false)
        }
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.animTop.visibility=View.VISIBLE

        val duration=3000L//binding.animTop.animation.duration;
        binding.animTop.playAnimation();
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
                binding.animTop.visibility=View.INVISIBLE;
                binding.animTop2.visibility=View.VISIBLE;
                val time=4000L//binding.animProgress.animation.duration;
                binding.animTop2.playAnimation();
            binding.animProgress.visibility=View.VISIBLE
            binding.animProgress.playAnimation()
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    startActivity(Intent(this@SplashScreenActivity,WelcomeActivity::class.java));
                    finish()
                },time);

        },duration);
    }


}