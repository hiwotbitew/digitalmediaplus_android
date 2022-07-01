package com.hiwot.digitalmediaplus

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hiwot.digitalmediaplus.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var takePicture: ActivityResultLauncher<Uri>
    //private lateinit var choosePicture: ActivityResultLauncher<Uri>
    private lateinit var binding:ActivityRegisterBinding
    private var avatar:Bitmap?=null
    private val cameraPermission=199
    private var imageURI:Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        takePicture=registerForActivityResult(ActivityResultContracts.TakePicture()){
            if(it){
                Log.e("hiwot","successfully retreived")
                Glide.with(this@RegisterActivity)
                    .asBitmap()
                    .load(imageURI)
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            avatar=resource
                            binding.imgProfile.setImageBitmap(avatar)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            Log.e("hiwot","image cleared from mem")
                        }

                    })
            }
        }
        //choosePicture=registerForActivityResult(ActivityResultContracts.)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnNext.setOnClickListener { validateInputs() }
        binding.imgProfile.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this@RegisterActivity,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
                openCamera()
            }else{
                requestPermissions(Array<String>(1){Manifest.permission.CAMERA},cameraPermission)
            }
        }
    }

    private fun validateInputs() {
        val name=binding.etName.text
        val email=binding.etEmail.text
        val username=binding.etUsername.text
        if(name==null || name.length<3 || !name.trim().contains(' '))
            binding.etName.error="Invalid Full Name!"
        else if(email==null || email.length<3 || !email.contains('@') || !email.contains('.'))
            binding.etEmail.error="Invalid Email Address!"
        else if(username==null || username.length<4 || username.contains(' '))
            binding.etUsername.error="Invalid Username!"
        else if(avatar==null)
            Toast.makeText(this, "Take a selfie to complete registration", Toast.LENGTH_LONG)
                .show()
        else {
            val intent=Intent(this@RegisterActivity,RegisterNextActivity::class.java)
            intent.putExtra("email",email.toString())
            intent.putExtra("username",username.toString())
            intent.putExtra("name",name.toString())
            intent.putExtra("avatar",imageURI)
            startActivity(intent);
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==cameraPermission){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                openCamera();
            }else{
                Toast.makeText(
                    this@RegisterActivity,
                    "We need camera permission to take your profile picture",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun openCamera() {
        val filename = "temporary_file.jpg"

        // Get the correct media Uri for your Android build version
        val imageUri2 =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
        val imageDetails = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, filename)
        }

        // Try to create a Uri for your image file
        this.contentResolver.insert(imageUri2, imageDetails).let {
            // Save the generated Uri using our placeholder from before
            this@RegisterActivity.imageURI = it

            // Capture your image
            takePicture.launch(this@RegisterActivity.imageURI)
        } ?: run {
            // Something went wrong
        }
    }
}