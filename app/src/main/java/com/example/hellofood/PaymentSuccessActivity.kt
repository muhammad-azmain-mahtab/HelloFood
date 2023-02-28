package com.example.hellofood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hellofood.databinding.ActivityPaymentSuccessBinding

class PaymentSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val orderType = intent.getStringExtra("orderType").toString()
//        if (orderType == "50"){
//            binding.textView5.text = "Estimated Delivery Time : "
//        }else{
//            binding.textView5.text = "Estimated Pick Up Time : "
//        }

        val pickuptime = intent.getStringExtra("pickuptime").toString()
        binding.tvPickUpTime.text = pickuptime

        binding.btnBackToMenu.setOnClickListener {
            val intent = Intent(this@PaymentSuccessActivity, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }
}