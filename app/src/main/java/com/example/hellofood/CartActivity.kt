package com.example.hellofood

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hellofood.databinding.ActivityCartBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class CartActivity : AppCompatActivity(), CartAdapter.OnItemClickListener {
    private lateinit var binding: ActivityCartBinding
    private lateinit var database: DatabaseReference
    private lateinit var cartItemList: ArrayList<CartItem>
    private var itemCost = 0
    private var shopId = ""
    private lateinit var q: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getShopId()

        binding.imgDelete.setOnClickListener {
            deleteWholeCart()
        }

        binding.imgBack.setOnClickListener {
            super.onBackPressed()
        }

        binding.btnConfirmCart.setOnClickListener {
            database =
                FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("orders").child(Firebase.auth.currentUser!!.uid)

            val ref = database.orderByChild("status").equalTo("1")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val alertDialog = AlertDialog.Builder(this@CartActivity)
                        alertDialog.setTitle("Opps!!")
                        alertDialog.setMessage("It seems that you already have an active order.\nPlease collect your order first or cancel it before making another order.")
                        alertDialog.setPositiveButton(
                            "Ok",
                            { dialogInterface: DialogInterface, i: Int -> })
                        alertDialog.show()
                    } else {
                        //println("parent" + shopId)
                        startActivity(
                            Intent(
                                this@CartActivity,
                                ConfirmOrderPaymentActivity::class.java
                            ).putExtra("shopId", shopId)
                        )
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

        cartItemList = arrayListOf<CartItem>()
        q = arrayListOf<Int>()
        getCartData()
    }

    private fun getShopId() {
        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("carts").child(Firebase.auth.currentUser!!.uid)
        val ref = database.orderByKey().limitToLast(1)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (Snap in snapshot.children) {
                        shopId = Snap.child("shop").getValue(String::class.java).toString()
                        //println(shopId)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


private fun deleteWholeCart() {
    database =
        FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("carts")
    database.child(Firebase.auth.currentUser!!.uid).removeValue()
    Toast.makeText(this, "Removed all items from cart", Toast.LENGTH_SHORT).show()
    val intent = Intent(applicationContext, MainActivity::class.java)
    startActivity(intent)
    finishAffinity()
}

fun getCartData() {
    itemCost = 0
    database =
        FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("carts").child(Firebase.auth.currentUser!!.uid)
    val postListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                cartItemList.clear()
                for (cartSnapshot in snapshot.children) {
                    val cartItem = cartSnapshot.getValue(CartItem::class.java)
                    cartItemList.add(cartItem!!)
                }
                binding.tvEmptyCart.isVisible = false
                binding.btnConfirmCart.isEnabled = true
                binding.imgDelete.isVisible = true

                binding.recviewCartItems.adapter =
                    CartAdapter(cartItemList, this@CartActivity)
                binding.recviewCartItems.layoutManager =
                    LinearLayoutManager(this@CartActivity, RecyclerView.VERTICAL, false)
            }
        }

        override fun onCancelled(error: DatabaseError) {

        }

    }
    database.addValueEventListener(postListener)
}

override fun onPlusItemClick(position: Int) {
    val clickedItem = cartItemList[position]
    val qtn = clickedItem.quantity?.toInt()

    database =
        FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("carts").child(Firebase.auth.currentUser!!.uid)
    database.orderByChild("title").equalTo(clickedItem.title)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    val parent = it.key.toString()
                    val qty = qtn?.plus(1)
                    clickedItem.quantity = "$qty"
                    //println("item " + position + " quantity " + qty)
                    updateCartData(parent, qty!!)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
}

override fun onMinusItemClick(position: Int) {
    val clickedItem = cartItemList[position]
    val qtn = clickedItem.quantity?.toInt()

    database =
        FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("carts").child(Firebase.auth.currentUser!!.uid)
    database.orderByChild("title").equalTo(clickedItem.title)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    val parent = it.key.toString()
                    if (qtn!! == 1) {
                        if (cartItemList.size > 1) {
                            val qty = qtn.minus(1)
                            clickedItem.quantity = "$qty"
                            database.child(parent).removeValue()
                        } else {
                            deleteWholeCart()
                        }
                    }
                    if (qtn > 1) {
                        val qty = qtn.minus(1)
                        clickedItem.quantity = "$qty"
                        //println("item " + position + " quantity " + qty)
                        updateCartData(parent, qty)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
}

private fun updateCartData(itemId: String, qty: Int) {
    database =
        FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("carts")
    val qa = qty.toString()
    val quantity = mapOf<String, String>(
        "quantity" to qa
    )
    database.child(Firebase.auth.currentUser!!.uid).child(itemId)
        .updateChildren(quantity)
        .addOnSuccessListener {
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
        }
}

}