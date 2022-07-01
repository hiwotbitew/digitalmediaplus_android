package com.hiwot.digitalmediaplus

import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hiwot.digitalmediaplus.databinding.ActivityHomeBinding
import com.hiwot.digitalmediaplus.databinding.DialogSetAvatarBinding
import com.hiwot.digitalmediaplus.databinding.UiSideNavBinding
import com.hiwot.digitalmediaplus.db.Persist
import com.hiwot.digitalmediaplus.model.User
import com.hiwot.digitalmediaplus.util.Constants
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import com.yarolegovich.slidingrootnav.callback.DragListener

class HomeActivity : AppCompatActivity(),DragListener {
    lateinit var nav: SlidingRootNav
    lateinit var v:UiSideNavBinding
    lateinit var binding:ActivityHomeBinding
    lateinit var user:User
    var avatar:Bitmap?=null
    private lateinit var takePicture: ActivityResultLauncher<Uri>
    private val cameraPermission=199
    private var imageURI:Uri?=null
    private lateinit var dialog:AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.run{
            WindowCompat.setDecorFitsSystemWindows(this, false)
        }
        supportActionBar?.hide()
        takePicture=registerForActivityResult(ActivityResultContracts.TakePicture()){
            if(it){
                Log.e("hiwot","successfully retreived")
                Glide.with(this@HomeActivity)
                    .asBitmap()
                    .load(imageURI)
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            avatar=resource
                            v.imgProfile.setImageBitmap(avatar)
                            Persist(this@HomeActivity).save(Constants.KEY_AVATAR,avatar!!)
                            Toast.makeText(
                                this@HomeActivity,
                                "Your profile is set successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            //v..setImageBitmap(avatar)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            Log.e("hiwot","image cleared from mem")
                        }

                    })
            }
        }
        binding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref=Persist(this)
        user=User(
            pref.load(Constants.KEY_USER_ID)!!.toInt(),
            pref.load(Constants.KEY_USERNAME)!!,
            pref.load(Constants.KEY_FULL_NAME)!!,
            pref.load(Constants.KEY_EMAIL)!!,
            pref.load(Constants.KEY_PASSWORD)!!,
            pref.load(Constants.KEY_PHONE)!!,
            pref.load(Constants.KEY_BALANCE)!!
        )
        avatar=pref.loadBitmap(Constants.KEY_AVATAR)
        if(avatar==null){
            requestAvatar();
        }
        val inflater=LayoutInflater.from(this);
        v=UiSideNavBinding.inflate(inflater)
        nav = SlidingRootNavBuilder(this)
            .withMenuOpened(false)
            .addDragListener(this)
            .withContentClickableWhenMenuOpened(false)
            .withRootViewElevation(10)
            .withSavedState(savedInstanceState)
            .withRootViewScale(0.75f)
            .withMenuView(v.root)
            .inject()
        initSlidingNav()

    }

    private fun requestAvatar() {
        val view=DialogSetAvatarBinding.inflate(LayoutInflater.from(this))
        dialog=AlertDialog.Builder(this)
            .setView(view.root)
            .setCancelable(false)
            .create()
        view.imgProfile.setOnClickListener {
            if(checkSelfPermission(android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
                dialog.cancel()
                openCamera();
            }else{
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA),cameraPermission)
            }
        }
        dialog.show()
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
            this@HomeActivity.imageURI = it

            // Capture your image
            takePicture.launch(this@HomeActivity.imageURI)
        } ?: run {
            // Something went wrong
        }
    }

    private fun initSlidingNav() {

        //val imgBack=nav.layout.findViewById<ImageView>(R.id.img_back)
        val imgBack=v.imgBack;
        val dashboard=v.txvDashboard;
        val bulkSms=v.txvSmsBulk;
        val voiceSms=v.txvVoice;
        val vouchers=v.txvVoucher;
        val reseller=v.txvReseller;
        val tvSubs=v.txvSms;
        val airtime=v.txvRecharge;
        val bills=v.txvBills;
        val pin=v.txvPinPurchase;
        val emailHosting=v.txvEmailHosting;
        val webHosting=v.txvWebHosting;
        val longCode=v.txvLongCode;
        val ussd=v.txvUssd;
        val itunes=v.txvItunes;
        val logout=v.txvLogout;
        val imgProfile=v.imgProfile;
        val tvName=v.txvName;

        tvName.text=user.fullName
        v.tvBalance.text=user.balance

        dashboard.setBackgroundColor(Color.BLUE)
        imgBack.setOnClickListener { nav.closeMenu(true) }
        binding.menu.setOnClickListener {
            if(nav.isMenuClosed)
                nav.openMenu(true)
            else
                nav.closeMenu(true);

        }

    }

    override fun onDrag(progress: Float) {

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
                    this@HomeActivity,
                    "We need camera permission to take your profile picture",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}