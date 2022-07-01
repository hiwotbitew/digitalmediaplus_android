package com.hiwot.digitalmediaplus

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.hiwot.digitalmediaplus.databinding.ActivityRegisterNextBinding

class RegisterNextActivity : AppCompatActivity() {
    private var avatar: Uri?= null
    lateinit var b:ActivityRegisterNextBinding
    private var email:String? = null
    private var name:String? = null
    private var username:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b= ActivityRegisterNextBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.btnFinish.setOnClickListener { finish() }
        if(intent.hasExtra("name"))
            name=intent.getStringExtra("name")
        if(intent.hasExtra("email"))
            email=intent.getStringExtra("email")
        if(intent.hasExtra("username"))
            username=intent.getStringExtra("username")
        if(intent.hasExtra("avatar"))
            avatar=intent.getParcelableExtra<Uri>("avatar")
        if(name==null || email == null || username==null || avatar==null) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish();
        }


        b.btnFinish.setOnClickListener { validateInputs() }

    }

    private fun validateInputs() {
        val phone=b.etPhone.text
        val password=b.etPassword.text
        val password2=b.etPassword2.text
        if(phone==null || phone.length<5)
            b.etPhone.error="Invalid phone number!"
        else if(phone.startsWith("0"))
            b.etPhone.error="Remove leading zero from your number";
        else if(password!=null)
            b.etPassword.error="Password is required!"
        else if(password2==null)
            b.etPassword2.error="Confirm your password!"
        else if(password.toString() != password2.toString())
            b.etPassword2.error="Your passwords do not match!"
        else{
            if(passwordCheck(password.toString())){
                val p=b.ccp.selectedCountryCodeWithPlus+phone.toString().trim()
                register(username,phone.toString(),email,name,password);
            }
        }
    }

    private fun register(
        username: String?,
        phone: String,
        email: String?,
        name: String?,
        password: Nothing?
    ) {
        TODO()
    }

    private fun passwordCheck(password: String): Boolean {
        if(password.length<8){
            b.etPassword.error="Password should be at least 8 characters long!"
            return false
        }
        var isCaps:Boolean=false
        var isLow:Boolean=false
        var hasNumeric:Boolean=false
        for(c in password.toCharArray()){
            if(c.isUpperCase() && c.isLetter())
                isCaps=true;
            if(c.isLetter() && c.isLowerCase())
                isLow=true
            if(c.isDigit())
                hasNumeric=true
        }
        if(!isCaps)
            b.etPassword.error="Include at least 1 Upper-Case letter"
        if(!isLow)
            b.etPassword.error="Include at least 1 Lower-case letter"
        if(!hasNumeric)
            b.etPassword.error="Include at least 1 digit"
        return isCaps && isLow && hasNumeric;
    }

}