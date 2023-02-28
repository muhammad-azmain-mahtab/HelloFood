package com.example.hellofood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hellofood.databinding.FragmentReviewOrderBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class ReviewOrderFragment : Fragment() {
    private var _binding: FragmentReviewOrderBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var cartItemList: ArrayList<CartItem>
    private lateinit var quantity: ArrayList<String>
    private lateinit var price: ArrayList<String>
    private lateinit var reviewOrderItemList: ArrayList<CartItem>
    private var itemCost = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReviewOrderBinding.inflate(inflater, container, false)

        val shopId = activity?.intent?.getStringExtra("shopId").toString()
        if(shopId == "cafeAust"){
            binding.radioPickup.isChecked = true
            binding.radioDelivery.isEnabled = false
            binding.tvVat.text = "0"
            val total = binding.tvItemCost.text.toString().toInt() + 0
            binding.tvTotalCost.text = total.toString()
        }

        cartItemList = arrayListOf<CartItem>()
        quantity = arrayListOf<String>()
        price = arrayListOf<String>()
        reviewOrderItemList = arrayListOf<CartItem>()
        getCartData()

        binding.radioDelivery.setOnClickListener {
                binding.tvVat.text = "50"
                val total = binding.tvItemCost.text.toString().toInt() + 50
                binding.tvTotalCost.text = total.toString()
        }

        binding.radioPickup.setOnClickListener {
            binding.tvVat.text = "0"
            val total = binding.tvItemCost.text.toString().toInt() + 0
            binding.tvTotalCost.text = total.toString()
        }

        binding.btnConfirmOrder.setOnClickListener {
//            val fragment = PaymentFragment()
//            val bundle = Bundle()
//            bundle.putString("totalPrice", binding.tvTotalCost.text.toString())
//            fragment.arguments = bundle
            val totalPrice =
                (activity as ConfirmOrderPaymentActivity).findViewById<TextView>(R.id.tv_totalPriceSave)
            totalPrice.text = binding.tvTotalCost.text.toString()
            (activity as ConfirmOrderPaymentActivity).replaceFragment(PaymentFragment())

            val storedId =
                (activity as ConfirmOrderPaymentActivity).findViewById<TextView>(R.id.tv_store_ordertype)
            storedId.text = binding.tvVat.text

            val checkOutTitle =
                (activity as ConfirmOrderPaymentActivity).findViewById<RadioButton>(R.id.radio_CheckOut)
            checkOutTitle.isChecked = true

//            val ReviewTitle = (activity as ConfirmOrderPaymentActivity).findViewById<TextView>(R.id.titleReview)
//            val paymentTitle = (activity as ConfirmOrderPaymentActivity).findViewById<TextView>(R.id.titlePayment)
//            ReviewTitle?.setTextColor(Color.parseColor("#000000"))
//            paymentTitle?.setTextColor(Color.parseColor("#F9282D"))
        }

        return binding.root
    }

    fun getCartData() {
        //itemCost = 0
        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("carts").child(Firebase.auth.currentUser!!.uid)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    cartItemList.clear()
                    for (cartSnapshot in snapshot.children) {
                        val cartItem = cartSnapshot.getValue(CartItem::class.java)
                        cartItemList.add(cartItem!!)
                        reviewOrderItemList.add(cartItem!!)
                    }
                    itemCost = 0
                    //println("cartItem array size " + cartItemList.size)
                    for (i in 0..(cartItemList.size - 1)) {
                        quantity.add(cartItemList[i].quantity!!)
                        price.add(cartItemList[i].price!!)
                        var qtn = quantity[i].toInt()
                        var prc = price[i].toInt()
                        itemCost += qtn * prc
                    }
                    println(itemCost)
                    binding.tvItemCost.text = itemCost.toString()
                    val totalCost =
                        binding.tvVat.text.toString().toInt() + binding.tvItemCost.text.toString()
                            .toInt()
                    binding.tvTotalCost.text = totalCost.toString()

                    //println("quantity array size " + quantity.size)

                    binding.recviewOrderItems.adapter =
                        ReviewOrderAdapter(reviewOrderItemList)
                    binding.recviewOrderItems.layoutManager =
                        LinearLayoutManager(context, RecyclerView.VERTICAL, false)
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