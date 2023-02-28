package com.example.hellofood

import android.content.Intent
import android.location.Location.distanceBetween
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.hellofood.databinding.ActivityPaymentSuccessDeliveryBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class PaymentSuccessDeliveryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityPaymentSuccessDeliveryBinding
    private var userLat = 0.0
    private var userLong = 0.0
    private var shopLat = 0.0
    private var shopLong = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentSuccessDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //println("oncreate start")

        //println("oncreate end")
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        //println("onmapready start")
        binding.btnBackToMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        val deliveryTime = intent.getStringExtra("deliveryTime").toString()
        binding.tvPickUpTime.text = deliveryTime

        //userLat = getUserLat()
        //println("userLat3: "+userLat)

        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")
        database.child(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
            if (it.exists()) {
                userLat = it.child("locationLat").value.toString().toDouble()
                userLong = it.child("locationLong").value.toString().toDouble()
                val user = LatLng(userLat, userLong)
                googleMap.addMarker(MarkerOptions().position(user).title("User"))?.showInfoWindow()
            }
        }

        val shopId = intent.getStringExtra("shopId").toString()
        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("shops")
        database.child(shopId).get().addOnSuccessListener {
            if (it.exists()) {
                shopLat = it.child("locationLat").value.toString().toDouble()
                shopLong = it.child("locationLong").value.toString().toDouble()
                val shop = LatLng(shopLat, shopLong)
                googleMap.addMarker(MarkerOptions().position(shop).title("Restaurant"))?.showInfoWindow()
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shop, 13f))
            }
        }

//        val dhaka = LatLng(23.8103, 90.4125)
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dhaka, 11f))

        //getDistance(userLat, userLong, shopLat, shopLong)

        //updateReceiveTimeForOrder()
        //println("onmapready end")
    }

//    private fun getUserLat(): Double {
//        database =
//            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
//                .getReference("users")
//        database.child(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
//            if (it.exists()) {
//                userLat = it.child("locationLat").value.toString().toDouble()
//                println("userLat1: " + userLat)
//            }
//        }
//        println("userLat2: " + userLat)
//        return userLat
//    }
//
//    private fun getDistance(userLat: Double, userLong: Double, shopLat: Double, shopLong: Double) {
//        val result = FloatArray(1)
//        distanceBetween(userLat, userLong, shopLat, shopLong, result)
//        Toast.makeText(this, "${result[0]} meters", Toast.LENGTH_LONG).show()
//        println("${result[0]} meters")
//    }

}