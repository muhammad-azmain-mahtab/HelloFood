package com.example.hellofood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.hellofood.databinding.ActivityConfirmOrderPaymentBinding

class ConfirmOrderPaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmOrderPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmOrderPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            super.onBackPressed()
        }
        replaceFragment(ReviewOrderFragment())

        binding.radioOrderReview.setOnClickListener {
            replaceFragment(ReviewOrderFragment())
        }
    }

//    override fun onBackPressed() {
//
////        val paymentFragment: PaymentFragment? =
////            supportFragmentManager.findFragmentByTag("PaymentFragment") as PaymentFragment?
////        if (paymentFragment != null && paymentFragment.isVisible()) {
////            replaceFragment(ReviewOrderFragment())
////        }else{
////            startActivity(Intent(this,MainActivity::class.java))
////        }
//         startActivity(Intent(this, MainActivity::class.java))
//    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        //println("$fragment")
        fragmentTransaction.commit()
    }
}