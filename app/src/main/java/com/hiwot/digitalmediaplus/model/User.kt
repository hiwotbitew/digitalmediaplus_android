package com.hiwot.digitalmediaplus.model
import kotlinx.serialization.Serializable

@Serializable
data class User(val id:Int,val username:String,val fullName:String,val email:String,val password:String,val phone:String,val balance:String)
