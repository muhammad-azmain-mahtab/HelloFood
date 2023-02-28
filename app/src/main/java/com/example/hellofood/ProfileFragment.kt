package com.example.hellofood

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hellofood.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.root.setOnClickListener {
            clearFocus()
        }

        binding.btnUpdate.setOnClickListener {
            updateData()
        }

        binding.btnDelete.setOnClickListener {
            deleteUser()
        }

        getUserData()

        return binding.root
    }

    private fun deleteUser() {
        //auth = Firebase.auth
        //val user = auth.currentUser!!
        val alertDialog = AlertDialog.Builder(context)

        alertDialog.setMessage("Are You Sure? ")
        alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
            auth = Firebase.auth
            val user = auth.currentUser!!
            //println(user.toString() + " " + user.uid)
            user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    database =
                        FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("users")
                    println(user.toString() + " " + user.uid)
                    database.child(user.uid).removeValue()
                    Toast.makeText(context, "Account Deleted", Toast.LENGTH_SHORT).show()
                    auth.signOut()
                    startActivity(Intent(context, MainActivity::class.java))
                    activity?.finish()
                } else {
                    Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                }
            }
        })
        alertDialog.setNegativeButton("No", { dialogInterface: DialogInterface, i: Int -> })
        alertDialog.show()
    }

    private fun updateData() {
        clearFocus()
        val firstName = binding.tilFirstname.editText?.text.toString()
        val lastName = binding.tilLastname.editText?.text.toString()
        val phone = binding.tilPhone.editText?.text.toString()
        val address = binding.tilAddress.editText?.text.toString()

        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")

        val user = mapOf<String, String>(
            "firstName" to firstName,
            "lastName" to lastName,
            "mobile" to phone,
            "address" to address
        )
        database.child(Firebase.auth.currentUser!!.uid).updateChildren(user).addOnSuccessListener {
            Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Profile Update Failed. Please Try Again.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun clearFocus() {
        binding.tilFirstname.clearFocus()
        binding.tilLastname.clearFocus()
        binding.tilEmail.clearFocus()
        binding.tilPhone.clearFocus()
        binding.tilAddress.clearFocus()
    }

    private fun getUserData() {
        database =
            FirebaseDatabase.getInstance("https://hellofood-c1d85-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")
        database.child(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
            if (it.exists()) {
                val firstName = it.child("firstName").value
                val lastName = it.child("lastName").value
                val email = it.child("email").value
                val phone = it.child("mobile").value
                val address = it.child("address").value

                binding.tilFirstname.editText?.setText(firstName.toString())
                binding.tilLastname.editText?.setText(lastName.toString())
                binding.tilEmail.editText?.setText(email.toString())
                binding.tilPhone.editText?.setText(phone.toString())
                binding.tilAddress.editText?.setText(address.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}