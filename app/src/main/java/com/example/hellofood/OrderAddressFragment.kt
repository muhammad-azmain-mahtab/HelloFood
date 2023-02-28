package com.example.hellofood

import android.app.AlertDialog
import android.content.DialogInterface
import android.location.Location
import android.location.Location.distanceBetween
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_order_address.*

class OrderAddressFragment : Fragment() {
    private lateinit var database: DatabaseReference

    private val callback = OnMapReadyCallback { googleMap ->

        val dhaka = LatLng(23.8103, 90.4125)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dhaka, 10f))

        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users").child(Firebase.auth.currentUser!!.uid)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.childrenCount.toString() == "7") {
                        database =
                            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("users")
                        database.child(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                            if (it.exists()) {
                                val userLat = it.child("locationLat").value.toString().toDouble()
                                val userLong = it.child("locationLong").value.toString().toDouble()
                                val currentAddress = LatLng(userLat, userLong)
                                googleMap.addMarker(MarkerOptions().position(currentAddress).title("Order Address"))
                                tv_addressLat.text = userLat.toString()
                                tv_addressLong.text = userLong.toString()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        database.addListenerForSingleValueEvent(postListener)

        btn_setAddress.setOnClickListener {
            val lat = tv_addressLat.text.toString()
            val long = tv_addressLong.text.toString()

            database =
                FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("users")

            val user = mapOf<String, String>(
                "locationLat" to lat,
                "locationLong" to long
            )
            database.child(Firebase.auth.currentUser!!.uid).updateChildren(user).addOnSuccessListener {
                Toast.makeText(context, "Location Updated", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Location Update Failed. Please Try Again.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        googleMap.setOnMapClickListener {
            //val markerOptions = MarkerOptions().position(it).title(it.latitude.toString() + " , " + it.longitude.toString())
            val markerOptions = MarkerOptions().position(it).title("Order Address")
            googleMap.clear()
            googleMap.addMarker(markerOptions)
            tv_addressLat.text = it.latitude.toString()
            tv_addressLong.text = it.longitude.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}