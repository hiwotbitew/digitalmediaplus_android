package com.hiwot.digitalmediaplus.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.airbnb.lottie.LottieAnimationView
import com.hiwot.digitalmediaplus.R

class ViewPagerAdapter(context: Context,list:ArrayList<View>):PagerAdapter() {
    private val items:ArrayList<View>
            = list
    override fun getCount(): Int {
        return items.size;
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view=items[position];
        container.addView(view)

        return view;
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
    public fun getItem(position:Int):View{
        return items[position];
    }
}