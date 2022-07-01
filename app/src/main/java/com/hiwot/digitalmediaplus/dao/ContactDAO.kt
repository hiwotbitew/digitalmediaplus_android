package com.hiwot.digitalmediaplus.dao

import androidx.room.*
import com.hiwot.digitalmediaplus.model.Contact

@Dao
interface ContactDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(contact:Contact)

    @Delete
    fun delete(contact:Contact)

    @Query ("SELECT * FROM contact WHERE name= :name")
    fun getContacts(name:String):List<Contact>

    @Query("SELECT DISTINCT name FROM contact ORDER BY name")
    fun getSavedContacts():List<String>

    @Update
    fun update(contact:Contact)
}