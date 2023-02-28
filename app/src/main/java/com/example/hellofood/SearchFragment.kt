package com.example.hellofood

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hellofood.databinding.FragmentSearchBinding
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class SearchFragment : Fragment(), ShopAdapter.OnItemClickListener {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var shopList: ArrayList<Shop>
    private lateinit var filterList: ArrayList<Shop>
    private lateinit var parent: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        (activity as HomeActivity).img_menu.isGone = true
        (activity as HomeActivity).tvHome.isGone = true

        binding.imgGoback.setOnClickListener {
            binding.searchBar.clearFocus()
            (activity as HomeActivity).img_menu.isGone = false
            (activity as HomeActivity).tvHome.isGone = false
            (activity as HomeActivity).replaceFragment(HomeMenuFragment(), "Home")
        }

        binding.searchBar.requestFocus()

        filterList = ArrayList<Shop>()
        shopList = ArrayList<Shop>()

        getData()

        binding.searchBar.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterList.clear()
                val searchText = query!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    binding.tvNothing.isGone = true
                    shopList.forEach {
                        if (it.title?.lowercase(Locale.getDefault())!!.contains(searchText)) {
                            filterList.add(it)
                            binding.recviewSearch.adapter =
                                ShopAdapter(filterList, this@SearchFragment)
                            binding.recviewSearch.layoutManager = LinearLayoutManager(context)
                        } else {

                        }
                    }
                } else {
                    filterList.clear()
                    ShopAdapter(filterList, this@SearchFragment).notifyDataSetChanged()
                    binding.tvNothing.isVisible = true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    shopList.forEach {
                        if (it.title?.lowercase(Locale.getDefault())!!.contains(searchText)) {
                            filterList.add(it)
                            binding.recviewSearch.adapter =
                                ShopAdapter(filterList, this@SearchFragment)
                            binding.recviewSearch.layoutManager = LinearLayoutManager(context)
                        } else {

                        }
                    }
                } else {
                    filterList.clear()
                    ShopAdapter(filterList, this@SearchFragment).notifyDataSetChanged()
                }
                return false
            }

        })

        return binding.root
    }

    private fun getData() {
        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("shops")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    shopList.clear()
                    for (shopSnapshot in snapshot.children) {
                        val shop = shopSnapshot.getValue(Shop::class.java)
                        shopList.add(shop!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onShopItemClick(position: Int) {
        val clickedItem = filterList[position]
        val shopName = clickedItem.title
        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("shops")
        database.orderByChild("title").equalTo(shopName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children.forEach {
                        parent = it.key.toString()
                        val intent = Intent(
                            context, ShopMenuActivity::class.java
                        ).putExtra("shop_id", parent)
                        startActivity(intent)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}