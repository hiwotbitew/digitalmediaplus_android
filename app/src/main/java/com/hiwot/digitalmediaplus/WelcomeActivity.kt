package com.hiwot.digitalmediaplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.hiwot.digitalmediaplus.adapter.ViewPagerAdapter
import com.hiwot.digitalmediaplus.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    lateinit var binding:ActivityWelcomeBinding
    lateinit var adapter:ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.run{
            WindowCompat.setDecorFitsSystemWindows(this, false)
        }
        supportActionBar?.hide()
        binding= ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter=ViewPagerAdapter(this,createItems());
        binding.viewPager.adapter=adapter;
        binding.viewPager.currentItem=0;
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                adapter.getItem(position).findViewById<LottieAnimationView>(R.id.animItem).playAnimation()
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
        Handler(Looper.getMainLooper()).postDelayed(Runnable { nextPage() },2000)

        binding.btnLogin.setOnClickListener { startActivity(Intent(this@WelcomeActivity,LoginActivity::class.java)) }
        binding.btnSignup.setOnClickListener { startActivity(Intent(this@WelcomeActivity,RegisterActivity::class.java)) }
    }
    private fun nextPage(){
        //Log.e("hiwot","looper running, "+binding.viewPager.currentItem)
        adapter.notifyDataSetChanged()
        if (binding.viewPager.currentItem==8)
            binding.viewPager.currentItem= 0
        else binding.viewPager.currentItem=++binding.viewPager.currentItem;

       // Toast.makeText(this,"current item is: "+binding.viewPager.currentItem,Toast.LENGTH_LONG).show()
        Handler(Looper.getMainLooper()).postDelayed(Runnable { nextPage() },2000)
    }
    private fun createItems():ArrayList<View>{
        val items=ArrayList<View>(9);
        var v1=LayoutInflater.from(this).inflate(R.layout.item_product_thread,null,false);
        v1.findViewById<TextView>(R.id.tvTitle).text="Bulk SMS"
        v1.findViewById<TextView>(R.id.description).text="Send bulk SMS at the speed of light!"
        v1.findViewById<LottieAnimationView>(R.id.animItem).setAnimation(R.raw.bulk_sms)
        items.add(v1)

        var v2=LayoutInflater.from(this).inflate(R.layout.item_product_thread,null,false);
        v2.findViewById<TextView>(R.id.tvTitle).text="Voice SMS"
        v2.findViewById<TextView>(R.id.description).text="Send your voice sms/robocalls campaigns. Cheap ,affordable but effective."
        v2.findViewById<LottieAnimationView>(R.id.animItem).setAnimation(R.raw.voice_sms)
        items.add(v2)

        var v3=LayoutInflater.from(this).inflate(R.layout.item_product_thread,null,false);
        v3.findViewById<TextView>(R.id.tvTitle).text="Airtime & Data"
        v3.findViewById<TextView>(R.id.description).text="Easy airtime and data top-up"
        v3.findViewById<LottieAnimationView>(R.id.animItem).setAnimation(R.raw.airtimedata)
        items.add(v3)

        val v4=LayoutInflater.from(this).inflate(R.layout.item_product_thread,null,false);
        v4.findViewById<TextView>(R.id.tvTitle).text="Bill Payments"
        v4.findViewById<TextView>(R.id.description).text="Pay your DSTV, GOTV AND STARTIMES subscription and Electricity bill payments."
        v4.findViewById<LottieAnimationView>(R.id.animItem).setAnimation(R.raw.bill)
        items.add(v4)

        val v5=LayoutInflater.from(this).inflate(R.layout.item_product_thread,null,false);
        v5.findViewById<TextView>(R.id.tvTitle).text="Web Hosting"
        v5.findViewById<TextView>(R.id.description).text="Efficient and cheap web hosting packages and domain name registration"
        v5.findViewById<LottieAnimationView>(R.id.animItem).setAnimation(R.raw.webhosting)
        items.add(v5)

        val v6=LayoutInflater.from(this).inflate(R.layout.item_product_thread,null,false);
        v6.findViewById<TextView>(R.id.tvTitle).text="Email Hosting"
        v6.findViewById<TextView>(R.id.description).text="Get a private email domain, secure and effective."
        v6.findViewById<LottieAnimationView>(R.id.animItem).setAnimation(R.raw.emailhosting)
        items.add(v6)

        val v7=LayoutInflater.from(this).inflate(R.layout.item_product_thread,null,false);
        v7.findViewById<TextView>(R.id.tvTitle).text="LongCode/Sim Hosting"
        v7.findViewById<TextView>(R.id.description).text="Get a dedicated longcode or use our shared longcode to receive any kind of data."
        v7.findViewById<LottieAnimationView>(R.id.animItem).setAnimation(R.raw.longcode)
        items.add(v7)

        val v8=LayoutInflater.from(this).inflate(R.layout.item_product_thread,null,false);
        v8.findViewById<TextView>(R.id.tvTitle).text="USSD"
        v8.findViewById<TextView>(R.id.description).text="Instant Channel set up on our USSD code. Use our robust ussd voting manager for your contests."
        v8.findViewById<LottieAnimationView>(R.id.animItem).setAnimation(R.raw.ussd)
        items.add(v8)

        val v9=LayoutInflater.from(this).inflate(R.layout.item_product_thread,null,false);
        v9.findViewById<TextView>(R.id.tvTitle).text="Itunes Cards"
        v9.findViewById<TextView>(R.id.description).text="Buy iTunes cards for your apple devices."
        v9.findViewById<LottieAnimationView>(R.id.animItem).setAnimation(R.raw.itunes)
        items.add(v9)

        return items;
    }
}