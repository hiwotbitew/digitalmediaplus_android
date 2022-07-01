package com.hiwot.digitalmediaplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.WindowCompat
import com.hiwot.digitalmediaplus.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.run{
            WindowCompat.setDecorFitsSystemWindows(this, false)
        }
        binding= ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.btnClick.setOnClickListener {
            Toast.makeText(this, "switching", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@MainActivity,HomeActivity::class.java))
        }
        startActivity(Intent(this@MainActivity,SplashScreenActivity::class.java));
        finish();


    }
}