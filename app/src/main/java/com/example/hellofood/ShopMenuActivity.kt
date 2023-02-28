package com.example.hellofood

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hellofood.databinding.ActivityShopMenuBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ShopMenuActivity : AppCompatActivity(), FoodItemAdapter.OnItemClickListener {
    private lateinit var binding: ActivityShopMenuBinding
    private lateinit var database: DatabaseReference
    private lateinit var shopId: String
    private lateinit var cartId: String
    private lateinit var foodItemList: ArrayList<FoodItem>
    private var userHasAddress: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            super.onBackPressed()
        }

        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("shops")
        shopId = intent.getStringExtra("shop_id").toString()

        getShopDeliveryTime()

        foodItemList = ArrayList<FoodItem>()

        if (shopId == "cafeAust"){
            getAustCafeItemData()
        }else{
            getHeaderData()
            getFoodItemData()
        }

    }

    private fun getShopDeliveryTime() {
        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")
        database.child(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
            if (it.exists()) {
                val loacationUser = Location("User")
                val locationShop = Location("Shop")
                val userLat = it.child("locationLat").value.toString().toDouble()
                val userLong = it.child("locationLong").value.toString().toDouble()
                loacationUser.latitude = userLat
                loacationUser.longitude = userLong
                database =
                    FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference("shops")
                database.child(shopId).get().addOnSuccessListener {
                    if (it.exists()) {
                        val shopLat = it.child("locationLat").value.toString().toDouble()
                        val shopLong = it.child("locationLong").value.toString().toDouble()
                        //println(shopLat)
                        //println(shopLong)
                        locationShop.latitude = shopLat
                        locationShop.longitude = shopLong
                        val distance = loacationUser.distanceTo(locationShop)
                        //println("distance is: "+distance)
                        val deliveryTime = (distance/60).toInt().toString()
                        //println("deliveryTime is: "+deliveryTime)
                        binding.tvDelTime.text = deliveryTime
                    }
                }
            }
        }
    }

    private fun getAustCafeItemData() {
        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("austCafe")
        database.get().addOnSuccessListener {
            if (it.exists()) {
                val category = it.child("category").value
                val imageUrl = it.child("imageUrl").value
                val price = it.child("price").value
                val title = it.child("title").value

                Glide.with(applicationContext).load(imageUrl).into(binding.imgShopImage);
                binding.tvShopName.text = title.toString()
                binding.tvShopCuisine.text = category.toString()
                binding.tvShopPrice.text = price.toString()
            }
        }

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                foodItemList.clear()
                if (snapshot.exists()) {
                    for (foodSnapshot in snapshot.children) {
                        val foodItem = foodSnapshot.getValue(FoodItem::class.java)
                        foodItemList.add(foodItem!!)
                    }
                    val foodApapter = FoodItemAdapter(foodItemList, this@ShopMenuActivity)
                    binding.recviewFoodItems.adapter = foodApapter
                    binding.recviewFoodItems.layoutManager =
                        LinearLayoutManager(this@ShopMenuActivity, RecyclerView.VERTICAL, false)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        database.child("foodItem").addValueEventListener(postListener)
    }

    override fun onFoodItemClick(position: Int) {
        val clickedItem = foodItemList[position]
        val imageUrl = clickedItem.imageUrl
        val title = clickedItem.title
        val price = clickedItem.price
        val quantity = clickedItem.quantity.toString()

        cartId = ""
        //var cartId = shopId

        checkIfUserHasAddress()
        if (userHasAddress){
            database =
                FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("carts").child(Firebase.auth.currentUser!!.uid)
            val ref = database.orderByKey().limitToLast(1)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (Snap in snapshot.children) {
                            cartId = Snap.child("shop").getValue(String::class.java).toString()
                            //println("in cart " + cartId)
                            addToCart(cartId, title, price, imageUrl, quantity, position)
                        }
                    }else{
                        cartId = shopId
                        addToCart(cartId, title, price, imageUrl, quantity, position)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }


//        println("in cart " + cartId + " current " + shopId)
//
//        if (shopId == cartId) {
//            if (!quantity.equals("0")) {
//                database =
//                    FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
//                        .getReference()
//
//                val cart = CartItem(title, price, imageUrl, quantity, shopId)
//                database.child("carts").child(Firebase.auth.currentUser!!.uid)
//                    .child("item$position")
//                    .setValue(cart)
//                    .addOnSuccessListener {
//                        Toast.makeText(this, "Added To Cart", Toast.LENGTH_LONG).show()
//                    }
//                    .addOnFailureListener {
//                        Toast.makeText(baseContext, "Failed.", Toast.LENGTH_LONG)
//                            .show()
//                    }
//            } else {
//                Toast.makeText(this, "Please Select Desired Quantity", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            Toast.makeText(this, "Please Select Items From Same Shop", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun checkIfUserHasAddress(){
        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(Firebase.auth.currentUser!!.uid)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.childrenCount.toString() != "7") {
                        val alertDialog = AlertDialog.Builder(this@ShopMenuActivity)
                        alertDialog.setMessage("Please select a delivery address from Order Address menu in order to get delivery service.")
                        alertDialog.setPositiveButton(
                            "Ok",
                            DialogInterface.OnClickListener { dialogInterface, i -> })
                        alertDialog.show()
                        userHasAddress = false
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        database.addListenerForSingleValueEvent(postListener)
    }

    private fun addToCart(
        cartId: String,
        title: String?,
        price: String?,
        imageUrl: String?,
        quantity: String?,
        position: Int
    ) {
        //println("in cart " + cartId + " current " + shopId)

        if (shopId == cartId) {
            if (!quantity.equals("0")) {
                database =
                    FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                        .getReference()

                val cart = CartItem(title, price, imageUrl, quantity, shopId)
                database.child("carts").child(Firebase.auth.currentUser!!.uid)
                    .child("item$position")
                    .setValue(cart)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Added To Cart", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(baseContext, "Failed.", Toast.LENGTH_LONG)
                            .show()
                    }
            } else {
                Toast.makeText(this, "Please Select Desired Quantity", Toast.LENGTH_SHORT).show()
            }
        } else {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Opps!!")
            alertDialog.setMessage("You can only order from one restaurant at a time.\nPlease clear your cart first in order to order from this restaurant.")
            alertDialog.setPositiveButton("Ok", { dialogInterface: DialogInterface, i: Int -> })
            alertDialog.show()
        }

    }

    override fun onPlusClick(position: Int) {
        val clickedItem = foodItemList[position]
        val qtn = clickedItem.quantity?.toInt()
        val qty = qtn?.plus(1)
        clickedItem.quantity = "$qty"
//        val foodApapter = FoodItemAdapter(foodItemList,this)
//        foodApapter.notifyItemChanged(position)
    }

    override fun onMinusClick(position: Int) {
        val clickedItem = foodItemList[position]
        val qtn = clickedItem.quantity?.toInt()
        if (qtn!! >= 1) {
            val qty = qtn.minus(1)
            clickedItem.quantity = "$qty"
//            val foodApapter = FoodItemAdapter(foodItemList, this)
//            foodApapter.notifyItemChanged(position)
        }
    }

    private fun getHeaderData() {
        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("shops")
        database.child(shopId).get().addOnSuccessListener {
            if (it.exists()) {
                val category = it.child("category").value
                val imageUrl = it.child("imageUrl").value
                val price = it.child("price").value
                val title = it.child("title").value

                Glide.with(applicationContext).load(imageUrl).into(binding.imgShopImage);
                binding.tvShopName.text = title.toString()
                binding.tvShopCuisine.text = category.toString()
                binding.tvShopPrice.text = price.toString()
            }
        }
    }

    private fun getFoodItemData() {
        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("shops").child(shopId).child("foodItem")
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                foodItemList.clear()
                if (snapshot.exists()) {
                    for (foodSnapshot in snapshot.children) {
                        val foodItem = foodSnapshot.getValue(FoodItem::class.java)
                        foodItemList.add(foodItem!!)
                    }
                    val foodApapter = FoodItemAdapter(foodItemList, this@ShopMenuActivity)
                    binding.recviewFoodItems.adapter = foodApapter
                    binding.recviewFoodItems.layoutManager =
                        LinearLayoutManager(this@ShopMenuActivity, RecyclerView.VERTICAL, false)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        database.addValueEventListener(postListener)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        finishAffinity()
        startActivity(intent)
        //super.onBackPressed()
    }
}