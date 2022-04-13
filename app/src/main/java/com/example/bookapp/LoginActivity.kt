package com.example.bookapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.bookapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private val email = ""
    private val password  = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize firebase
        firebaseAuth = FirebaseAuth.getInstance()

        //initialize progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)


        binding.loginBtn.setOnClickListener {
            validateData()
        }
        binding.noAccount.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }


    }

    private fun validateData() {
        loginUser()
//        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
//            binding.emailEt.error = "Invalid email format"
//        }
//        if (email.isEmpty()){
//            binding.emailEt.error = "email can not be empty"
//        }
//        else if(password.isEmpty()){
//            binding.passwordEt.error = "Password is required"
//        }
//        else{
//            loginUser()
//        }
    }

    private fun loginUser() {
        progressDialog.setMessage("logging in...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                checkUser()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed due to ${e}", Toast.LENGTH_LONG).show()
            }
    }

    private fun checkUser() {
        progressDialog.setMessage("Checking user...")
        val firebaseUser = firebaseAuth.currentUser!!
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    val userType = snapshot.child("userType").value
                    if(userType == "user"){
                        startActivity(Intent(this@LoginActivity, UserDashboardActivity::class.java))
                        finish()
                    }
                    else if (userType == "admin"){
                        startActivity(Intent(this@LoginActivity, AdminDashboardActivity::class.java))
                        finish()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginActivity, "Failed ${error}",
                        Toast.LENGTH_LONG).show()
                }

            })
    }
}