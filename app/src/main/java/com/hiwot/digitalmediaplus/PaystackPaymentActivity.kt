package com.hiwot.digitalmediaplus

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hiwot.digitalmediaplus.databinding.ActivityPaystackPaymentBinding

class PaystackPaymentActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPaystackPaymentBinding
    private val paymentMode= arrayOf("CARD","BANK","TRANSFER","USSD","QR")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPaystackPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
    private fun switchPayment(mode:Int){
        when(paymentMode[mode]){
            "CARD"-> {
                setupCardPayment()
                binding.bankPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.cardPayment.setBackgroundColor(getColor(R.color.active))
                binding.transferPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.ussdPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.qrPayment.setBackgroundColor(Color.TRANSPARENT)

                binding.layoutCard.visibility= View.VISIBLE
                binding.bankLayout.visibility=View.GONE
                binding.layoutTRANSFER.visibility=View.GONE
                binding.layoutQR.visibility=View.GONE
                binding.layoutUSSD.visibility=View.GONE

            }
            "BANK"-> {
                binding.bankPayment.setBackgroundColor(getColor(R.color.active))
                binding.cardPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.transferPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.ussdPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.qrPayment.setBackgroundColor(Color.TRANSPARENT)

                binding.layoutCard.visibility= View.GONE
                binding.bankLayout.visibility=View.VISIBLE
                binding.layoutTRANSFER.visibility=View.GONE
                binding.layoutQR.visibility=View.GONE
                binding.layoutUSSD.visibility=View.GONE
            }
            "TRANSFER"->{
                binding.bankPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.cardPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.transferPayment.setBackgroundColor(getColor(R.color.active))
                binding.ussdPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.qrPayment.setBackgroundColor(Color.TRANSPARENT)

                binding.layoutCard.visibility= View.GONE
                binding.bankLayout.visibility=View.GONE
                binding.layoutTRANSFER.visibility=View.VISIBLE
                binding.layoutQR.visibility=View.GONE
                binding.layoutUSSD.visibility=View.GONE
            }
            "USSD"->{
                binding.bankPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.cardPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.transferPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.ussdPayment.setBackgroundColor(getColor(R.color.active))
                binding.qrPayment.setBackgroundColor(Color.TRANSPARENT)


                binding.layoutCard.visibility= View.GONE
                binding.bankLayout.visibility=View.GONE
                binding.layoutTRANSFER.visibility=View.GONE
                binding.layoutQR.visibility=View.GONE
                binding.layoutUSSD.visibility=View.VISIBLE
            }
            "QR"->{
                binding.bankPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.cardPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.transferPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.ussdPayment.setBackgroundColor(Color.TRANSPARENT)
                binding.qrPayment.setBackgroundColor(getColor(R.color.active))

                binding.layoutCard.visibility= View.GONE
                binding.bankLayout.visibility=View.GONE
                binding.layoutTRANSFER.visibility=View.GONE
                binding.layoutQR.visibility=View.VISIBLE
                binding.layoutUSSD.visibility=View.GONE
            }

        }
    }

    private fun setupCardPayment() {

    }
}