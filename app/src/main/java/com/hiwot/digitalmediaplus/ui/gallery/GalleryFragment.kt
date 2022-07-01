package com.hiwot.digitalmediaplus.ui.gallery

import android.app.AlertDialog
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.hiwot.digitalmediaplus.R
import com.hiwot.digitalmediaplus.api.APIUtil
import com.hiwot.digitalmediaplus.api.Service
import com.hiwot.digitalmediaplus.databinding.FragmentGalleryBinding
import com.hiwot.digitalmediaplus.databinding.ThreadManageSmsBinding
import com.hiwot.digitalmediaplus.db.Persist
import com.hiwot.digitalmediaplus.ui.fragment.SendSMS
import com.hiwot.digitalmediaplus.util.Constants
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.IOException

class GalleryFragment : Fragment() {

    private lateinit var dialog: AlertDialog
    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val pref= Persist(requireContext())
        val username=pref.load(Constants.KEY_USERNAME)
        val password=pref.load(Constants.KEY_PASSWORD)
        val link="https://digitalmediaplus.org/newsite/unified/client/bulksms.dashboardmob.php?u=$username&p=$password";
        binding.btnSend.setOnClickListener {
            val fragment=SendSMS()
            val tx=parentFragmentManager.beginTransaction()
            tx.replace(R.id.nav_host_fragment_content_dashboard,fragment).commit()
        }
        binding.scrollRoot.setOnTouchListener(View.OnTouchListener { v, event ->
            val childV: View = binding.scrollView
            if (childV != null) {
                val l = IntArray(2)
                childV.getLocationOnScreen(l)
                val rect = RectF(
                    l[0].toFloat(),
                    l[1].toFloat(),
                    (l[0] + childV.width).toFloat(),
                    (l[1] + childV.height).toFloat()
                )
                if (rect.contains(event.x, event.y)) {
                    childV.parent
                        .requestDisallowInterceptTouchEvent(false)
                    childV.onTouchEvent(event)
                    return@OnTouchListener true
                }
            }
            childV!!.parent
                .requestDisallowInterceptTouchEvent(true)
            false
        })
        loadRecentMessages();
        loadDashboard();
        return root
    }

    private fun loadDashboard() {
        val pref=Persist(requireContext())
        val username=pref.load(Constants.KEY_USERNAME)
        val password=pref.load(Constants.KEY_PASSWORD)
        val userKeyId=pref.load(Constants.KEY_USER_ID)
        val map=HashMap<String,String>()
        map["username"]=username!!
        map["p"]=password!!
        map["user_id"]=userKeyId!!
        APIUtil.post(context,map,Service.apiSMSDashboard).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("hiwot","error",e)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val body=response.body?.string()
                    val root=JSONParser().parse(body) as JSONObject
                    if(root["status"]=="success"){
                        val smsCount=root["sms_count"].toString() ?: "0"
                        val smsFee=root["sms_fee"].toString() ?: "0.00"
                        requireActivity().runOnUiThread {
                            binding.tvSmsCount.text=smsCount
                            binding.tvExpCount.text=smsFee
                        }
                    }
                }catch (ex:Exception){
                    Log.e("hiwot","error",ex)
                }
            }

        });
    }

    private fun loadRecentMessages() {
        val pref=Persist(requireContext())
        val username=pref.load(Constants.KEY_USERNAME)
        val password=pref.load(Constants.KEY_PASSWORD)
        val userKeyId=pref.load(Constants.KEY_USER_ID)
        val map=HashMap<String,String>()
        map["username"]=username!!
        map["p"]=password!!
        map["user_id"]=userKeyId!!
        dialog=AlertDialog.Builder(requireContext())
            .setMessage("Please wait")
            .setCancelable(false)
            .create()
        dialog.show()
        APIUtil.post(context,map,Service.apiSMSList)
            .enqueue(object:Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("hiwot","error",e)
                    requireActivity().runOnUiThread {
                        dialog.cancel()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    try{
                        val body=response.body!!.string()
                        val root=JSONParser().parse(body) as JSONObject
                        if(root["status"]=="success"){
                            requireActivity().runOnUiThread {
                                dialog.dismiss()
                                val msgList=root["data"] as JSONArray
                                for(msg in msgList){
                                    msg as JSONObject
                                    val b=ThreadManageSmsBinding.inflate(LayoutInflater.from(context))
                                    val text=msg["spirit"].toString()
                                    val status=msg["status"].toString()
                                    b.tvMessage.text="Message: "+if(text.length>120) text.substring(0,120) else text
                                    b.tvFee.text="Fee: "+msg["value"].toString()
                                    b.tvStatus.text="Status: "+if(status=="0") "sent" else "failed"
                                    b.tvSent.text="Schedule: ${msg["schedule"].toString()}"
                                    b.tvFailed.text="Failed: "+msg["failed"]?.toString() ?:"0"
                                    b.tvPending.text="Pending: "+msg["remaining"].toString()
                                    b.tvTotal.text="Total: "+msg["total"].toString()
                                    binding.parentSms.addView(b.root)
                                }
                            }

                        }else{
                            requireActivity().runOnUiThread {
                                dialog.cancel()
                            }
                        }
                    }catch (ex:Exception){
                        Log.e("hiwot","error",ex)
                        requireActivity().runOnUiThread {
                            dialog.cancel()
                        }
                    }
                }

            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}