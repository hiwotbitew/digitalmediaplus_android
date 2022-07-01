package com.hiwot.digitalmediaplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.core.view.WindowCompat
import com.hiwot.digitalmediaplus.api.APIUtil
import com.hiwot.digitalmediaplus.api.Service
import com.hiwot.digitalmediaplus.databinding.ActivityLoginBinding
import com.hiwot.digitalmediaplus.db.Persist
import com.hiwot.digitalmediaplus.model.User
import com.hiwot.digitalmediaplus.util.Constants
import okhttp3.Call
import okhttp3.Response
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.IOException
import javax.security.auth.callback.Callback

class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.run{
            WindowCompat.setDecorFitsSystemWindows(this, false)
        }
        supportActionBar?.hide()
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener {
            checkInputs()
        }
    }

    private fun checkInputs() {
        val username=binding.etUsername.text
        val password=binding.etPassword.text
        if(username==null || username.length<3 || username.contains(' '))
            binding.layoutUsername.error="Invalid username!"
        else if(password==null || password.length<4)
            binding.layoutPassword.error="Invalid password!"
        else{
            processLogin(username.toString(),password.toString())
        }
    }

    private fun processLogin(username: String, password: String) {
        val map=HashMap<String,String>()
        map.put("username",username)
        map.put("p",password)
        APIUtil.post(this,map, Service.apiLOGIN).enqueue(object : Callback, okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("hiwot","error requesting login",e)
                runOnUiThread {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error connecting to server!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful){
                    val body=response.body?.string()
                    Log.e("hiwot","response: $body")
                    val parser=JSONParser()
                    val root:JSONObject=parser.parse(body) as JSONObject
                    val status=root["status"] as String
                    if("success"==status){
                        val obj:JSONObject=root["user"] as JSONObject
                        val user=User(obj["id"].toString().toInt(),obj["username"].toString(),obj["fullname"].toString(), obj["email"].toString(),password,obj["phone"].toString(),obj["balance"].toString())
                        val pref=Persist(this@LoginActivity);
                        pref.save(Constants.KEY_USER_ID,user.id.toString())
                        pref.save(Constants.KEY_USERNAME,user.username)
                        pref.save(Constants.KEY_EMAIL,user.email)
                        pref.save(Constants.KEY_FULL_NAME,user.fullName)
                        pref.save(Constants.KEY_PASSWORD,user.password)
                        pref.save(Constants.KEY_PHONE,user.phone)
                        pref.save(Constants.KEY_BALANCE,user.balance)
                        runOnUiThread {
                            val intent=Intent(this@LoginActivity,DashboardActivity::class.java)
                            intent.putExtra("user_id",user.id)
                            startActivity(intent)
                            finish()
                        }

                    }else{
                        Log.e("hiwot","error login: ${root["message"]}")
                        runOnUiThread {
                            Toast.makeText(
                                this@LoginActivity,
                                "Error: ${root["message"]}",
                                Toast.LENGTH_LONG
                            ).show() }
                    }

                }
            }

        })
    }
}