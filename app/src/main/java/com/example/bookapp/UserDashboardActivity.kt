package com.example.bookapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookapp.databinding.ActivityUserDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class UserDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDashboardBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserDashboardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.logOut.setOnClickListener {
            checkUser()
        }
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            binding.userTitle.text = "not logged In"
        }else{
            val email = firebaseUser.email
            binding.emailTitle.text = email
        }
    }
}