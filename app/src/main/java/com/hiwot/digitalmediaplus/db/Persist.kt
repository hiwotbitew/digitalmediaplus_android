package com.hiwot.digitalmediaplus.db

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayOutputStream
import java.lang.Exception

class Persist(var context:Context) {
    val pref="DMP"
    private var sharedPref:SharedPreferences?=null
        fun save(key:String,value:String){
            if(sharedPref==null)
                sharedPref=context.getSharedPreferences(pref,Context.MODE_PRIVATE)
            sharedPref!!.edit().putString(key,value).apply()
        }
    fun load(key:String):String?{
        if(sharedPref==null)
            sharedPref=context.getSharedPreferences(pref,Context.MODE_PRIVATE)
        return sharedPref!!.getString(key,null)
    }
    fun save(key:String,value:Boolean){
        if(sharedPref==null)
            sharedPref=context.getSharedPreferences(pref,Context.MODE_PRIVATE)
        sharedPref!!.edit().putBoolean(key,value).apply()
    }
    fun save(key:String,bitmap: Bitmap){
        val stream=ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,90,stream)
        val bytes=stream.toByteArray()
        context.openFileOutput(key,Context.MODE_PRIVATE).write(bytes)
    }
    fun loadBitmap(key:String):Bitmap?{
        try {
            val bytes = context.openFileInput(key).readBytes()
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }catch (e:Exception){
            Log.e("hiwot","error loading file $key",e)
        }
        return null;
    }
}