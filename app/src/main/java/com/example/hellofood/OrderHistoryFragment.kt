package com.example.hellofood

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hellofood.databinding.FragmentOrderHistoryBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class OrderHistoryFragment : Fragment() {
    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var OrderHistoryList: ArrayList<OrderHistoryItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)

        OrderHistoryList = ArrayList<OrderHistoryItem>()

        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("payments")
        val ref = database.orderByChild("uid").equalTo(Firebase.auth.currentUser!!.uid)
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    OrderHistoryList.clear()
                    for (Snapshot in snapshot.children) {
                        val Item = Snapshot.getValue(OrderHistoryItem::class.java)
                        println(Item)
                        OrderHistoryList.add(Item!!)
                    }
                    binding.recviewOrderHistoryItems.adapter =
                        OrderHistoryAdapter(OrderHistoryList)
                    binding.recviewOrderHistoryItems.layoutManager =
                        LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        ref.addListenerForSingleValueEvent(postListener)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}