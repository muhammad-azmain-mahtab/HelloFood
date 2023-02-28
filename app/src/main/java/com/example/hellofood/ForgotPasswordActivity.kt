package com.example.hellofood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.hellofood.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnReset.setOnClickListener {
            val email = binding.etResetEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show()
            } else {
                resetPassword(email)
            }
        }
    }

    private fun resetPassword(email: String) {
        binding.progressBar.visibility = View.VISIBLE

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext,
                        "Check Your Email To Reset Your Password.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    binding.progressBar.visibility = View.GONE
                    binding.etResetEmail.text = null
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Something Went Wrong. Please Try Again",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
    }
}