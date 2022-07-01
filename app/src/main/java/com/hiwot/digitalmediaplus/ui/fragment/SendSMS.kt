package com.hiwot.digitalmediaplus.ui.fragment

import android.app.AlertDialog
import android.graphics.RectF
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hiwot.digitalmediaplus.R
import com.hiwot.digitalmediaplus.api.APIUtil
import com.hiwot.digitalmediaplus.api.Service
import com.hiwot.digitalmediaplus.databinding.DialogContactGroupBinding
import com.hiwot.digitalmediaplus.databinding.DialogPreviewSmsBinding
import com.hiwot.digitalmediaplus.databinding.FragmentSendSMSBinding
import com.hiwot.digitalmediaplus.databinding.ThreadContactItemBinding
import com.hiwot.digitalmediaplus.db.MyRoomDatabase
import com.hiwot.digitalmediaplus.db.Persist
import com.hiwot.digitalmediaplus.model.Contact
import com.hiwot.digitalmediaplus.ui.gallery.GalleryFragment
import com.hiwot.digitalmediaplus.util.Constants
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SendSMS.newInstance] factory method to
 * create an instance of this fragment.
 */
class SendSMS : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding:FragmentSendSMSBinding

    private lateinit var smsType:String
    private lateinit var coverage:String
    private lateinit var senderID:String
    private lateinit var recpientSettings:String
    private lateinit var contactEntryMethod:String
    private lateinit var sms:String
    private lateinit var schedule:String
    private var numberList=ArrayList<String>()
    private lateinit var loadingDialog:AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSendSMSBinding.inflate(inflater)
        binding.spinContact.onItemSelectedListener=itemSelected()
        binding.btnSendSMS.setOnClickListener {
            validateFields(true)
        }
        binding.btnPreview.setOnClickListener { validateFields(false) }
        Constants.hideKeypad(context,binding.etSMS)
        Constants.hideKeypad(context,binding.etSenderId)
        binding.etSMS.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val count=binding.etSMS.text.length
                val smsCount:Int=(count/160) + 1;
                binding.tvSmsCount.text="$count Characters ($smsCount SMS)"
            }

        })
        loadingDialog=AlertDialog.Builder(context)
            .setTitle("Please wait")
            .setMessage("Loading your contacts")
            .create()
        loadSavedContacts()
        return  binding.root//inflater.inflate(R.layout.fragment_send_s_m_s, container, false)
    }

    private fun loadSavedContacts() {
        val id=Persist(requireContext()).load(Constants.KEY_USER_ID)
        val map=HashMap<String,String>()
        map["user_id"]="4"
        loadingDialog.show()
        APIUtil.post(context,map, Service.apiListContact).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("hiwot","unsuccesful connection",e)
                requireActivity().runOnUiThread{
                    Toast.makeText(context, "Connection unsuccessful", Toast.LENGTH_LONG).show()
                    loadingDialog.cancel()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val body=response.body!!.string()
                    Log.e("hiwot","resp: $body")
                    val root=JSONParser().parse(body) as JSONObject
                    if(root["status"]=="success"){
                        val results=root["result"] as JSONArray
                        val contactDAO=MyRoomDatabase.getDatabase(requireContext()).ContactDAO()
                        for(result in results){
                            result as JSONObject
                            val name=result["name"] as String
                            val mixed=result["contacts"] as String
                            var contacts=mixed.split("rn")
                            if(contacts.isEmpty())
                                contacts=mixed.split(",")
                            for(c in contacts){
                                contactDAO.insert(Contact(0,name,c.trim()))
                            }
                        }
                        requireActivity().runOnUiThread{
                            //Toast.makeText(context, "Connection unsuccessful", Toast.LENGTH_LONG).show()
                            loadingDialog.cancel()
                        }
                    }else{
                        requireActivity().runOnUiThread{
                            Toast.makeText(context, "Something went wrong while fetching contacts", Toast.LENGTH_LONG).show()
                            loadingDialog.cancel()
                        }
                    }
                }catch (ex:Exception){
                    Log.e("hiwot","error",ex)
                    requireActivity().runOnUiThread{
                        Toast.makeText(context, "Something went wrong while fetching contacts", Toast.LENGTH_LONG).show()
                        loadingDialog.cancel()
                    }
                }
            }
        })
    }

    private fun validateFields(send:Boolean) {
        val smsType=binding.spinSMSType.selectedItem.toString()
        if(smsType=="--Select An Option--"){
            Toast.makeText(context, "Select SMS type!", Toast.LENGTH_SHORT).show()
            return
        }
        val coverage=binding.spinCountryCode.selectedCountryCode
        val senderId=binding.etSenderId.text
        if(senderId.isEmpty()){
            binding.etSenderId.error="Sender id is required!"
            return
        }
        if(senderId.length>11){
            binding.etSenderId.error="Sender id is too long!"
            return
        }
        val receiptSettings=binding.spinSettings.selectedItem.toString()
        if(numberList.size==0){
            Toast.makeText(
                context,
                "Please make sure to add receiver phone numbers",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val sms=binding.etSMS.text
        if(sms.isEmpty()){
            binding.etSMS.error="SMS is empty!"
        }
        val schedule=binding.spinSchedule.selectedItem.toString()
        if(schedule=="--Select An Option--"){
            Toast.makeText(
                context,
                "Please select schedule to send your message",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if(!send){
            val bind=DialogPreviewSmsBinding.inflate(LayoutInflater.from(requireContext()))
            val d=AlertDialog.Builder(requireContext())
                .setView(bind.root)
                .create()
            bind.tvSMS.text=sms
            bind.tvReceiverCount.text="${numberList.size}"
            bind.tvSenderId.text=senderId.toString()
            bind.tvSchedule.text=schedule
            bind.btnClose.setOnClickListener {
                d.cancel()
            }
            d.show()
        }else{
            var recpients=String()
            for(num in numberList){
                recpients+="\r\n$num"
            }
            sendMessage(smsType,coverage,senderId.toString(),receiptSettings,contactEntryMethod,recpients,sms.toString(),schedule)
        }
    }

    private fun sendMessage(smsType:String,coverage:String,senderID:String,settings:String,chooseContact:String,recipients:String,message:String,schedule:String) {
        val pref=Persist(requireContext())
        val username=pref.load(Constants.KEY_USERNAME)
        val password=pref.load(Constants.KEY_PASSWORD)
        val userKeyId=pref.load(Constants.KEY_USER_ID)
        val map=HashMap<String,String>()
        map["smstype"] = "0"//if(smsType=="text") "0" else "1"
        map["coverage"]=coverage
        map["senderid"]=senderID
        map["settings"]="0"//if(settings=="--Do Nothing--") "0" else if(settings=="Remove Invalid Numbers") "1" else if(settings=="Remove Duplicate Numbers") "2" else "3"
        map["deliveryreports"]="1"
        map["choosecontacts"]="2"
        map["recipients"]=recipients
        map["asksave"]="no"
        map["textmessage"]=message
        map["sendoptions"]=if(schedule=="Send Now") "1" else "2"
        map["scheduledate"]=""
        map["smspages"]="${(message.length/160)+1}"
        map["username"]=username!!
        map["p"]=password!!
        map["user_id"]=userKeyId!!
        loadingDialog.setMessage("Please wait")
        loadingDialog.show()
        APIUtil.post(context,map,"https://digitalmediaplus.org/newsite/unified/client/functions/grocerystoremob.php?item=sendbulksms")
            .enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("hiwot","error sending request",e)
                    requireActivity().runOnUiThread {
                        Toast.makeText(context, "Request Failed! Please check your network", Toast.LENGTH_LONG).show()
                        loadingDialog.cancel()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    try{
                        val body=response.body!!.string()
                        Log.e("hiwot","resp: $body")
                        val root=JSONParser().parse(body) as JSONObject
                        if(root["status"]=="success"){
                            Constants.showDialog(requireContext(),true,root["message"].toString())
                            requireActivity().runOnUiThread {
                                loadingDialog.cancel()
                                val fragment=GalleryFragment()
                                val tx=parentFragmentManager.beginTransaction()
                                tx.replace(R.id.nav_host_fragment_content_dashboard,fragment).commit()
                            }
                        }else{
                            Log.e("hiwot","error ${root["message"]}")
                            Constants.showDialog(requireContext(),false,root["message"].toString())
                            requireActivity().runOnUiThread {
                                Toast.makeText(context, "Error: ${root["message"]}", Toast.LENGTH_LONG).show()
                                loadingDialog.cancel()
                            }
                        }
                    }catch (ex:Exception){
                        Log.e("hiwot","error sending request",ex)
                        requireActivity().runOnUiThread {
                            Toast.makeText(context, "Request Failed! Something went wrong!", Toast.LENGTH_LONG).show()
                            loadingDialog.cancel()
                        }
                    }
                }

            })
    }

    private fun itemSelected(): AdapterView.OnItemSelectedListener? {
        return object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                contactEntryMethod=binding.spinContact.selectedItem.toString()
                if(contactEntryMethod=="Use a saved contact group"){
                    launchContactEntryDialog(true)
                }else if(contactEntryMethod=="Upload a CSV/txt file"){

                }else if( contactEntryMethod=="Enter numbers manually"){
                    launchContactEntryDialog(false)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun launchContactEntryDialog(fromSaved: Boolean) {
        val b=DialogContactGroupBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog=AlertDialog.Builder(requireContext())
            .setView(b.root)
            .setCancelable(false)
            .create()
        var contactSaveList:List<String>?=null
        val manualList=ArrayList<String>()
        var contactList:List<Contact>?=null
        var saveNew=false
        var saveUpdated=false
        Constants.hideKeypad(context,b.etContactGroupName)
        Constants.hideKeypad(context,b.etReceivers)

        b.root.setOnTouchListener(OnTouchListener { v, event ->
            val childV: View = b.scrollview
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


        if(!fromSaved){
            b.layoutGroups.visibility=View.GONE
            val contactDAO=MyRoomDatabase.getDatabase(requireContext()).ContactDAO()

            b.btnAdd.setOnClickListener{
                val num=b.etReceivers.text
                if(num.length<8)
                    b.etReceivers.error="Invalid Number!"
                else{
                    val bind=ThreadContactItemBinding.inflate(LayoutInflater.from(requireContext()))
                    bind.tvPhone.text=num
                    manualList.add(num.toString())
                    bind.btnDelete.setOnClickListener {
                        bind.root.visibility=View.GONE
                        manualList.remove(num.toString())
                    }
                    b.contactParent.addView(bind.root)
                }
            }
            b.spinSaveOptions.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val option=b.spinSaveOptions.selectedItem.toString()
                    if(option=="Create New Contact List" || option=="Update Contact List"){
                        b.etContactGroupName.visibility=View.VISIBLE
                        saveNew=true

                    }else{
                        b.etContactGroupName.visibility=View.GONE
                        saveNew=false
                        saveUpdated = false
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
            b.btnNext.setOnClickListener {
                if(saveNew){
                    val contactName=b.etContactGroupName.text
                    if(contactName.isEmpty() || contactName.length<3){
                        b.etContactGroupName.error="Contact Name too short!"
                        return@setOnClickListener
                    }
                    for(num in manualList){
                        val c=Contact(0,contactName.toString(),num)
                        contactDAO.insert(c)
                    }
                }
                else if(saveUpdated){
                    val contactName=b.spinContactGroup.selectedItem.toString()
                    if(contactName=="--Select Contact Group--"){
                        Toast.makeText(context, "Please select a contact group", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }
                    for(num in manualList){
                        val c=Contact(0,contactName.toString(),num)
                        contactDAO.insert(c)
                    }
                }
                numberList.addAll(manualList)
                dialog.cancel()
            }
        }else{
            val contactDAO=MyRoomDatabase.getDatabase(requireContext()).ContactDAO()
            contactSaveList=contactDAO.getSavedContacts()
            val spinnerAdapter=ArrayAdapter<String>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,contactSaveList)
            spinnerAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
            b.spinContactGroup.adapter=spinnerAdapter
            b.spinContactGroup.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val name:String=b.spinContactGroup.selectedItem.toString()
                    if(name=="--Select Contact Group--")
                        return
                    contactList=contactDAO.getContacts(name)
                    b.contactParent.removeAllViews()
                    for(contact in contactList!!){
                        val bind=ThreadContactItemBinding.inflate(LayoutInflater.from(requireContext()))
                        bind.tvPhone.text=contact.number
                        bind.btnDelete.setOnClickListener {
                            bind.root.visibility=View.GONE
                            contactDAO.delete(contact)
                        }
                        b.contactParent.addView(bind.root)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
            b.btnAdd.setOnClickListener{
                val num=b.etReceivers.text
                if(num.length<8)
                    b.etReceivers.error="Invalid Number!"
                else{
                    val bind=ThreadContactItemBinding.inflate(LayoutInflater.from(requireContext()))
                    bind.tvPhone.text=num
                    manualList.add(num.toString())
                    bind.btnDelete.setOnClickListener {
                        bind.root.visibility=View.GONE
                        manualList.remove(num.toString())
                    }
                    b.contactParent.addView(bind.root)
                }
            }
            b.spinSaveOptions.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val option=b.spinSaveOptions.selectedItem.toString()
                    if(option=="Create New Contact List"){
                        b.etContactGroupName.visibility=View.VISIBLE
                        saveNew=true

                    }else{
                        b.etContactGroupName.visibility=View.GONE
                        saveNew=false
                        saveUpdated = option=="Update Contact List"
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
            b.btnNext.setOnClickListener {
                if(saveNew){
                    val contactName=b.etContactGroupName.text
                    if(contactName.isEmpty() || contactName.length<3){
                        b.etContactGroupName.error="Contact Name too short!"
                        return@setOnClickListener
                    }
                    for(num in manualList){
                        val c=Contact(0,contactName.toString(),num)
                        contactDAO.insert(c)
                    }
                }
                else if(saveUpdated){
                    val contactName=b.spinContactGroup.selectedItem.toString()
                    if(contactName=="--Select Contact Group--"){
                        Toast.makeText(context, "Please select a contact group", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }
                    for(num in manualList){
                        val c=Contact(0,contactName.toString(),num)
                        contactDAO.insert(c)
                    }
                }
                for(c in contactList!!){
                    manualList.add(c.number)
                }
                numberList.addAll(manualList)
                dialog.cancel()
            }
        }
        dialog.show()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SendSMS.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SendSMS().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}