package com.hiwot.digitalmediaplus

import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.get
import com.hiwot.digitalmediaplus.databinding.ActivityDashboardBinding
import com.hiwot.digitalmediaplus.db.Persist
import com.hiwot.digitalmediaplus.util.Constants
import de.hdodenhof.circleimageview.CircleImageView

class DashboardActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.run{
            WindowCompat.setDecorFitsSystemWindows(this, false)
        }
        supportActionBar?.hide()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarDashboard.toolbar)
        //setActionBar(binding.appBarDashboard.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_sms, R.id.nav_airtime
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val pref=Persist(this)
        val name=pref.load(Constants.KEY_FULL_NAME)
        val email=pref.load(Constants.KEY_EMAIL)

        val avatar=pref.loadBitmap(Constants.KEY_AVATAR)
        val balance=pref.load(Constants.KEY_BALANCE)
        navView.getHeaderView(0).findViewById<TextView>(R.id.tvBalance).text=balance
        navView.getHeaderView(0).findViewById<TextView>(R.id.tvName).text=name
        navView.getHeaderView(0).findViewById<TextView>(R.id.tvEmail).text=email
        navView.getHeaderView(0).findViewById<CircleImageView>(R.id.imageView).setImageBitmap(avatar)

        //navController..findViewById<TextView>(R.id.tvName).text="Nahom W."
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}