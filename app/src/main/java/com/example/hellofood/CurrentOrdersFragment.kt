package com.example.hellofood

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hellofood.databinding.FragmentCurrentOrdersBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_current_orders.*

class CurrentOrdersFragment : Fragment() {
    private var _binding: FragmentCurrentOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var orderItemList: ArrayList<OrderItem>
    private lateinit var quantity: ArrayList<String>
    private lateinit var price: ArrayList<String>
    private lateinit var orderDate: ArrayList<String>
    private lateinit var reviewOrderItemList: ArrayList<CartItem>
    private var itemCost = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentOrdersBinding.inflate(inflater, container, false)

        orderItemList = arrayListOf<OrderItem>()
        quantity = arrayListOf<String>()
        price = arrayListOf<String>()
        reviewOrderItemList = arrayListOf<CartItem>()
        orderDate = arrayListOf<String>()

        binding.tvNoOrder.isVisible = true

        binding.btnOrderCompete.setOnClickListener {
            updateStatus()
        }

        binding.btnCancelOrder.setOnClickListener {
            updatePaymentStatus()
        }

//        database =
//            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
//                .getReference("orders").child(Firebase.auth.currentUser!!.uid)
//        database.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()) {
//                    //orderDate.clear()
//                    for (Snapshot in snapshot.children) {
//                        val Item = Snapshot.getValue(Any::class.java)
//                        orderDate.add(Item.toString())
//                    }
//                    println(orderDate.size)
//                    println(orderDate[1])
//                    for (i in 0..(orderDate.size-1)){
//                        getOrderData(orderDate[i])
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })

        getOrderData()

        return binding.root
    }

    private fun updatePaymentStatus() {
        val orderDate = binding.tvOrderDateTime.text.toString()
        val orderId = binding.tvOrderId.text.toString()

        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("payments").child(orderId)//.child(orderDate)
        //val ref = database.orderByChild("orderDate").equalTo(orderDate)
        //val ref = database.orderByChild("orderId").equalTo(orderId)

        val order = mapOf<String, String>(
            "status" to "cancelled"
        )
        database.updateChildren(order).addOnSuccessListener {
            //Toast.makeText(context, "Order Cancelled", Toast.LENGTH_SHORT).show()
            database =
                FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("orders").child(Firebase.auth.currentUser!!.uid).child(orderId)//.child(orderDate)

            val or = mapOf<String, String>(
                "status" to "0"
            )
            database.updateChildren(or).addOnSuccessListener {
                //Toast.makeText(context, "Order Status Updated", Toast.LENGTH_SHORT).show()
                (activity as HomeActivity).replaceFragment(CurrentOrdersFragment(), "Current Order")
            }.addOnFailureListener {
                //Toast.makeText(context, "Update Failed.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            //Toast.makeText(context, "Cancel Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStatus() {
        val orderDate = binding.tvOrderDateTime.text.toString()
        val orderId = binding.tvOrderId.text.toString()

        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("payments").child(orderId)//.child(orderDate)

        val order = mapOf<String, String>(
            "status" to "successful"
        )
        database.updateChildren(order).addOnSuccessListener {
            //Toast.makeText(context, "Order Cancelled", Toast.LENGTH_SHORT).show()
            database =
                FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("orders").child(Firebase.auth.currentUser!!.uid).child(orderId)//.child(orderDate)

            val or = mapOf<String, String>(
                "status" to "2"
            )
            database.updateChildren(or).addOnSuccessListener {
                //Toast.makeText(context, "Order Status Updated", Toast.LENGTH_SHORT).show()
                (activity as HomeActivity).replaceFragment(CurrentOrdersFragment(), "Current Order")
            }.addOnFailureListener {
                //Toast.makeText(context, "Update Failed.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            //Toast.makeText(context, "Cancel Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getOrderData() {
        itemCost = 0
        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("orders").child(Firebase.auth.currentUser!!.uid)

        //val ref = database.orderByKey().limitToLast(1)
        val ref = database.orderByChild("status").equalTo("1")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    orderItemList.clear()
                    for (Snapshot in snapshot.children) {
                        //val Item = Snapshot.getValue(Any::class.java)
                        //println(Item)
                        val Item = Snapshot.getValue(OrderItem::class.java)
                        println(Item)
                        orderItemList.add(Item!!)
                        //orderItemList.add(Item!!)
                    }

                    //println("cartItem array size " + cartItemList.size)
                    //println(itemCost)
                    binding.tvNoOrder.isGone = true
                    binding.tvOrderId.text = orderItemList[0].orderId
                    //binding.tvItemCost.text = orderItemList[0].price!!
                    binding.tvItem.text = orderItemList[0].title!!
                    binding.tvOrderDateTime.text = orderItemList[0].date!!
                    binding.tvPickUpTime.text = orderItemList[0].receiveTime!!
                    binding.tvOrderType.text = orderItemList[0].orderType!!

                    if(binding.tvOrderType.text != "Delivery"){
                        val totalCost = orderItemList[0].price!!.toString().toInt()
                        binding.tvTotalCost.text = totalCost.toString()
//                        binding.tvVat.text = "0"
//                        binding.tv001.text = "Order Pick Up Time :"
                    }else{
                        val totalCost = orderItemList[0].price!!.toString().toInt()
                        binding.tvTotalCost.text = totalCost.toString()
//                        binding.tvVat.text = "50"
//                        binding.tv001.text = "Order Delivery Time :"
                    }

//                    val totalCost =
//                        binding.tvVat.text.toString().toInt() + binding.tvItemCost.text.toString()
//                            .toInt()
//                    binding.tvTotalCost.text = totalCost.toString()

                    //println("quantity array size " + quantity.size)

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