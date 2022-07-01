package com.hiwot.digitalmediaplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.hiwot.digitalmediaplus.databinding.ActivityFundBinding
import com.hiwot.digitalmediaplus.databinding.DialogConfirmPaymentBinding
import kotlin.math.ceil

class FundActivity : AppCompatActivity() {
    lateinit var binding:ActivityFundBinding
    val paymentMethods=arrayOf("PAYSTACK","VOGUE","BANK","TRANSFER")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFundBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Toast.makeText(
            this,
            "Please kindly note that we do not refund money after you have funded your account.",
            Toast.LENGTH_LONG
        ).show()
        binding.btnNext.setOnClickListener {
            validate()
        }
    }

    private fun validate() {
        val amount=binding.etAmount.text
        val id=binding.radioGroup.checkedRadioButtonId
        if(amount==null || amount.trim().isEmpty())
            binding.etAmount.error="Enter valid amount"
        else{
            val amountNaira=amount.toString().toDouble()
            val paymentMethod=when(id){
                binding.paystack.id->0
                binding.bank.id->2
                binding.vogue.id->1
                binding.transfer.id->3
                else -> 0
            }
            if(paymentMethod==0 || paymentMethod==1){
                if(amountNaira<100){
                    Toast.makeText(this, "Minimum amount for selected payment method is at least ${getString(R.string.naira)}100", Toast.LENGTH_SHORT).show()
                    binding.etAmount.error="Min amount is ${getString(R.string.naira)}100"
                }else{
                    val fee=ceil(amountNaira*0.015)
                    var stamp=0
                    if(amountNaira>1000)
                        stamp=1000
                }
            }
        }
    }
    private fun confirmPayment(intent: Intent,amount:Double,fee:Int,stamp:Int){
        val b=DialogConfirmPaymentBinding.bind(LayoutInflater.from(this).inflate(R.layout.dialog_confirm_payment,null,false))
        val dialog=AlertDialog.Builder(this)
            .setView(b.root)
            .setCancelable(true)
            .create()
        b.tvAmount2.text="${getString(R.string.naira)}$amount"
        b.tvFee2.text="${getString(R.string.naira)}$fee"
        b.tvStamp2.text="${getString(R.string.naira)}$stamp"
        b.tvTotal2.text="${getString(R.string.naira)}{$amount+$fee+$stamp}"
        b.btnNext.setOnClickListener {
            dialog.cancel()
            startActivity(intent)
        }
        dialog.show()

    }
}