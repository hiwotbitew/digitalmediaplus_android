package com.hiwot.digitalmediaplus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.hiwot.digitalmediaplus.databinding.ActivityPhoneVerificationBinding
import java.util.concurrent.TimeUnit

class PhoneVerificationActivity : AppCompatActivity() {
    private lateinit var phone:String
    private lateinit var binding:ActivityPhoneVerificationBinding
    private lateinit var fbAuth:FirebaseAuth
    private lateinit var phoneAuthCredential: PhoneAuthCredential
    private lateinit var verificationId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPhoneVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        phone=if(intent.getStringExtra("phone")==null) "null" else intent.getStringExtra("phone").toString()
        if(phone=="null"){
            Toast.makeText(this,"Invalid phone number provided",Toast.LENGTH_LONG).show()
            onBackPressed()
            finish()
        }
        binding.tvPhone.text=phone
        binding.btnVerify.setOnClickListener {
            val code=binding.etCode.text
            if(code?.trim()?.length==6){
                phoneAuthCredential=PhoneAuthProvider.getCredential(verificationId,code.toString())
                signInWithPhoneAuthCredentials(phoneAuthCredential)
            }else{
                binding.etCode.error="Invalid Code"
            }
        }
        sendCode()
    }

    private fun sendCode() {
        fbAuth= FirebaseAuth.getInstance()
        val options = PhoneAuthOptions.newBuilder(fbAuth)
            .setPhoneNumber(phone)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(PhoneAuthChange(this@PhoneVerificationActivity))          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    fun done(){

    }
    class PhoneAuthChange(context:PhoneVerificationActivity):PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        var instance:PhoneVerificationActivity = context
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            //done
            instance.signInWithPhoneAuthCredentials(p0)
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            if(p0 is FirebaseAuthInvalidCredentialsException){
                Toast.makeText(instance, "Something went wrong", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(
                    instance,
                    "SMS quota allocated for this api has been exceeded!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            Toast.makeText(instance, "Code Sent", Toast.LENGTH_LONG).show()
            instance.codeSent(p0)
        }
    }

    private fun codeSent(verificationId:String) {
        binding.etCode.isEnabled=true
        binding.btnVerify.isEnabled=true
        this.verificationId=verificationId
        val timer = object : CountDownTimer(60000,1000){
            override fun onTick(millisUntilFinished: Long) {
                binding.timer.text="00:"+millisUntilFinished/1000
            }

            override fun onFinish() {
                binding.tvResend.visibility= View.VISIBLE
            }

        }
        timer.start()
    }

    private fun signInWithPhoneAuthCredentials(p0: PhoneAuthCredential) {
        fbAuth.signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    //success
                }else{
                    Toast.makeText(this@PhoneVerificationActivity, "Wrong code", Toast.LENGTH_SHORT)
                        .show()
                    binding.etCode.error="Wrong code!"
                }
            }
    }

}