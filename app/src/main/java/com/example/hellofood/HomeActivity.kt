package com.example.hellofood

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.hellofood.databinding.ActivityHomeBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_order_address.*
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.drawerlayout)

        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(Firebase.auth.currentUser!!.uid)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.childrenCount.toString() != "7") {
                        val alertDialog = AlertDialog.Builder(this@HomeActivity)
                        alertDialog.setMessage("Please select a delivery address in order to continue.")
                        alertDialog.setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
                                openOrderAddress()
                            })
                        alertDialog.show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        database.addListenerForSingleValueEvent(postListener)

        auth = Firebase.auth
        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")

        getHeaderData()

        replaceFragment(HomeMenuFragment(), "Home")
        binding.navView.setCheckedItem(R.id.item1)

        toggle = ActionBarDrawerToggle(this, binding.drawerlayout, R.string.open, R.string.close)
        binding.drawerlayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.imgMenu.setOnClickListener {
            binding.drawerlayout.openDrawer(GravityCompat.START)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item1 -> replaceFragment(HomeMenuFragment(), "Home")
                R.id.item1_2 -> Toast.makeText(this, "item 1.2 clicked", Toast.LENGTH_SHORT).show()
                R.id.item1_3 -> openAustCanteenMenu()
                R.id.item2 -> replaceFragment(ProfileFragment(), "Profile")
                R.id.item2_1 -> openOrderAddress()
                R.id.item3 -> replaceFragment(CurrentOrdersFragment(), "Current Order")
                R.id.item4 -> replaceFragment(OrderHistoryFragment(), "Order History")
                R.id.item5 -> Toast.makeText(this, "item 5 clicked", Toast.LENGTH_SHORT).show()
                R.id.item6 -> Toast.makeText(this, "item 6 clicked", Toast.LENGTH_SHORT).show()
                R.id.item7 -> Toast.makeText(this, "item 7 clicked", Toast.LENGTH_SHORT).show()
                R.id.item8 -> Toast.makeText(this, "item 8 clicked", Toast.LENGTH_SHORT).show()
                R.id.item9 -> logOut()
            }
            true
        }

    }

    private fun openOrderAddress() {
        val alertDialog = AlertDialog.Builder(this)
        replaceFragment(OrderAddressFragment(), "Delivery Address")
        alertDialog.setMessage("Our service is only available in Dhaka City for now. Thank you for understanding. ")
        alertDialog.setPositiveButton(
            "Ok",
            DialogInterface.OnClickListener { dialogInterface, i -> })
        alertDialog.show()
    }

    private fun openAustCanteenMenu() {
        val intent = Intent(this, ShopMenuActivity::class.java).putExtra("shop_id", "cafeAust")
        startActivity(intent)
    }

    private fun getHeaderData() {
        database.child(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
            if (it.exists()) {
                val firstName = it.child("firstName").value
                val lastName = it.child("lastName").value
                val email = it.child("email").value

                val userName = findViewById<TextView>(R.id.tv_userName)
                val userEmail = findViewById<TextView>(R.id.tv_userEmail)

                userName.text = firstName.toString() + " " + lastName.toString()
                userEmail.text = email.toString()

                val last8 = userEmail.text.toString().takeLast(8)
                if (last8 == "aust.edu") {
                    binding.navView.menu.findItem(R.id.item1_3).isVisible = true
                }

            }
        }
    }

    private fun logOut() {
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setMessage("Log out of your account?")
        alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
            Firebase.auth.signOut()
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        })
        alertDialog.setNegativeButton("No", { dialogInterface: DialogInterface, i: Int -> })
        alertDialog.show()
    }

    fun replaceFragment(fragment: Fragment, title: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
        binding.drawerlayout.closeDrawer(GravityCompat.START)
        binding.tvHome.text = title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

}