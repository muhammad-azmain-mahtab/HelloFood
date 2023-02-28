package com.example.hellofood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.hellofood.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val loggedIn = auth.currentUser
        if (loggedIn != null) {
            if (loggedIn.isEmailVerified()) {
                goToHome()
            }
        }

        binding.root.setOnClickListener {
            clearFocus()
        }

        binding.btnLogin.setOnClickListener {
            clearFocus()
            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()
            if (email.length == 0 && password.length == 0) {
                Toast.makeText(
                    this,
                    "Please Enter Your Email Address And Password",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (email.length == 0) {
                Toast.makeText(this, "Please Enter Your Email Address", Toast.LENGTH_SHORT).show()
                binding.etLoginEmail.requestFocus()
            } else if (password.length == 0) {
                Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show()
                binding.etLoginPassword.requestFocus()
            } else {
                userLogin()
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.tvForgotpass.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    private fun clearFocus() {
        binding.etLoginEmail.clearFocus()
        binding.etLoginPassword.clearFocus()
    }

    private fun userLogin() {
        binding.progressBar.visibility = View.VISIBLE
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.text.toString().trim()

        if(email.equals("admin")&&password.equals("admin")){
            startActivity(
                Intent(this, AdminActivity::class.java)
            )
            finish()
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = Firebase.auth.currentUser
                    if (user!!.isEmailVerified()) {
                        Toast.makeText(
                            baseContext,
                            "Login Successful.Redirecting.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding.progressBar.visibility = View.GONE
                        goToHome()
                    } else {
                        Toast.makeText(baseContext, "Please Verify Your Email.", Toast.LENGTH_LONG)
                            .show()
                        binding.progressBar.visibility = View.GONE
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Login Failed. Please Check Your Credentials.",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
    }

    private fun goToHome() {
        startActivity(
            Intent(this, HomeActivity::class.java)
        )
        finish()
    }
}