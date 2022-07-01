package com.hiwot.digitalmediaplus.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hiwot.digitalmediaplus.dao.ContactDAO
import com.hiwot.digitalmediaplus.model.Contact

@Database (entities = arrayOf(Contact::class),version = 1,exportSchema = false)
public abstract class MyRoomDatabase:RoomDatabase() {
    abstract fun ContactDAO():ContactDAO
    companion object{

        @Volatile
        private var instance:MyRoomDatabase? = null
        fun getDatabase(context: Context):MyRoomDatabase{
            return instance?: synchronized(this){
                val instance2= Room.databaseBuilder(context.applicationContext,MyRoomDatabase::class.java,"main_db")
                    .allowMainThreadQueries()
                    .build()
                instance=instance2
                instance2
            }
        }
    }
}