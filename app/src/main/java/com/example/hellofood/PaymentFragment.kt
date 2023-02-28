package com.example.hellofood

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.hellofood.databinding.FragmentPaymentBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var cartItemList: ArrayList<CartItem>
    private lateinit var orderItemList: ArrayList<OrderItem>
    private var title = ""
    private var price = 0
    private var quantity = 0
    private var newTime = ""
    private var deliveryTime = ""
    private var newTime2 = ""
    private var orderId = ""
    private var shopId = ""
    private var userLat = 0.0
    private var userLong = 0.0
    private var shopLat = 0.0
    private var shopLong = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)

        shopId = activity?.intent?.getStringExtra("shopId").toString()
        //println("shop id "+ shopId)

        val storedId = (activity as ConfirmOrderPaymentActivity).findViewById<TextView>(R.id.tv_store_ordertype)
        if (storedId.text == "50") {
            orderId = "Delivery"
            getDeliveryTime()
        } else {
            orderId = "Pick Up"
        }

        binding.radioCash.setOnClickListener {
            binding.btnCashPay.isVisible = true
            binding.btnCardPay.isGone = true
            binding.constraintCard.isGone = true
        }

        binding.radioCard.setOnClickListener {
            binding.btnCashPay.isGone = true
            binding.btnCardPay.isVisible = true
            binding.constraintCard.isVisible = true
        }

        binding.radioBkash.setOnClickListener {
            binding.btnCashPay.isGone = true
            binding.btnCardPay.isGone = true
            binding.constraintCard.isGone = true
        }

        cartItemList = arrayListOf<CartItem>()
        orderItemList = arrayListOf<OrderItem>()

        binding.btnCashPay.setOnClickListener {
            val storedId =
                (activity as ConfirmOrderPaymentActivity).findViewById<TextView>(R.id.tv_store_ordertype)
            //database =
            //    FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
            //        .getReference("payments")
            database =
                FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("payments").push()
            val pushKey = database.key
            //println("key: " +pushKey)
            //val totalPrice = arguments?.getString("totalPrice")
            val totalPrice =
                (activity as ConfirmOrderPaymentActivity).findViewById<TextView>(R.id.tv_totalPriceSave).text.toString()

            val sdf = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.getDefault())
            val currentDateandTime = sdf.format(Calendar.getInstance().time)
            val dateFormated = currentDateandTime.toString()
            val date = Calendar.getInstance().time.toString()

            //println("Todays date is = $date")
            //println("Total price is =" +totalPrice)
            val payment = Payment(Firebase.auth.currentUser!!.uid, totalPrice, dateFormated, "ongoing",pushKey)
            //database.child(dateFormated.trim()).setValue(payment)
            database.setValue(payment)
                .addOnSuccessListener {
                    transferCartToOrder(dateFormated,pushKey)
                    if(orderId == "Delivery"){
                        val intent = Intent(context, PaymentSuccessDeliveryActivity::class.java).putExtra(
                            "shopId", shopId).putExtra(
                            "deliveryTime", newTime2)
                        startActivity(intent)
                    }else{
                        val intent = Intent(context, PaymentSuccessActivity::class.java).putExtra(
                            "pickuptime", newTime)
                        startActivity(intent)
                    }
                    //(activity as ConfirmOrderPaymentActivity).finishAffinity()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Payment Failed.", Toast.LENGTH_LONG).show()
                }
        }

        return binding.root

    }

    private fun getDeliveryTime() {

        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")
        database.child(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
            if (it.exists()) {
                val loacationUser = Location("User")
                val locationShop = Location("Shop")
                userLat = it.child("locationLat").value.toString().toDouble()
                userLong = it.child("locationLong").value.toString().toDouble()
                loacationUser.latitude = userLat
                loacationUser.longitude = userLong
                database =
                    FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference("shops")
                database.child(shopId).get().addOnSuccessListener {
                    if (it.exists()) {
                        shopLat = it.child("locationLat").value.toString().toDouble()
                        shopLong = it.child("locationLong").value.toString().toDouble()
                        println(shopLat)
                        println(shopLong)
                        locationShop.latitude = shopLat
                        locationShop.longitude = shopLong
                        val distance = loacationUser.distanceTo(locationShop)
                        println("distance is: "+distance)
                        deliveryTime = (distance/60).toInt().toString()
                        println("deliveryTime is: "+deliveryTime)

                        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                        val currentDateandTime = sdf.format(Calendar.getInstance().time)
                        val cal = Calendar.getInstance()
                        cal.time = sdf.parse(currentDateandTime)
                        cal.add(Calendar.MINUTE, deliveryTime.toInt())
                        newTime2 = sdf.format(cal.time).toString()
                    }
                }
            }
        }
    }

    private fun transferCartToOrder(date: String, pushKey: String?) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        val currentDateandTime = sdf.format(Calendar.getInstance().time)
        //println(currentDateandTime)
        val cal = Calendar.getInstance()
        cal.time = sdf.parse(currentDateandTime)
        cal.add(Calendar.MINUTE, 30)
        newTime = sdf.format(cal.time).toString()

        //println(newTime)

        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("carts").child(Firebase.auth.currentUser!!.uid)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (cartSnapshot in snapshot.children) {
                        val Item = cartSnapshot.getValue(CartItem::class.java)
                        cartItemList.add(Item!!)
                    }
                    database.removeValue()
                    for (i in 0..(cartItemList.size - 1)) {
                        price += cartItemList[i].price!!.toInt() * cartItemList[i].quantity!!.toInt()
                        title += cartItemList[i].title + "(x" + cartItemList[i].quantity + ")\n"
                        //title += cartItemList[i].title + " - " + "(x" + cartItemList[i].quantity + ")" + " - " + price + "Tk\n"
                        quantity += cartItemList[i].quantity!!.toInt()
                    }
                    if(orderId == "Pick Up"){
                        val item = OrderItem(title, price.toString(), quantity.toString(), "1", date, newTime, orderId, pushKey)

                        database =
                            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("orders").child(Firebase.auth.currentUser!!.uid)
                        database.child(pushKey!!).setValue(item)
                            .addOnSuccessListener {
                                //Toast.makeText(context, "Payment Successful.", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed.", Toast.LENGTH_LONG).show()
                            }
                    }else{
                        price += 50
                        val item = OrderItem(title, price.toString(), quantity.toString(), "1", date, newTime2, orderId, pushKey)

                        database =
                            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("orders").child(Firebase.auth.currentUser!!.uid)
                        database.child(pushKey!!).setValue(item)
                            .addOnSuccessListener {
                                //Toast.makeText(context, "Payment Successful.", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed.", Toast.LENGTH_LONG).show()
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}