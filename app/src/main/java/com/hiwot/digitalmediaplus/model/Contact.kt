package com.hiwot.digitalmediaplus.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity (tableName = "contact",indices = [Index(value = ["name","number"],unique = true)])
class Contact(
    @PrimaryKey(autoGenerate = true) var id:Int,

    @ColumnInfo (name="name")
    var name:String,
    @ColumnInfo (name="number")
    var number:String
)
