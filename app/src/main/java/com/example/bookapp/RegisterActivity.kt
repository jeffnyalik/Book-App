package com.example.bookapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.bookapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityRegisterBinding
    private  lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var username = ""
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Initialize firebase
        firebaseAuth = FirebaseAuth.getInstance()

        //initialize progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle back button
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        //already registered
        binding.alreadyReg.setOnClickListener {
            alreadyRegistered()
        }

        binding.registerBtn.setOnClickListener {
            validateData()
        }

    }

    private fun alreadyRegistered() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun validateData() {
        username = binding.usernameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val cPassword = binding.confirmPassEt.text.toString()

        if(TextUtils.isEmpty(username)){
            binding.usernameEt.error = "username is required"
        }
        else if(TextUtils.isEmpty(email)){
            binding.emailEt.error = "email address is required"
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.error = "Invalid email pattern"
        }
        else if(password.isEmpty()){
            binding.passwordEt.error = "password is required"
        }
        else if(cPassword.isEmpty()){
            binding.confirmPassEt.error = "confirm password is required"
        }
        else if(password.length < 6){
            Toast.makeText(this, "password must be six characters",
                Toast.LENGTH_LONG).show()
        }
        else if (password != cPassword){
            Toast.makeText(this, "password do not match",
                Toast.LENGTH_LONG).show()
        }
        else{
            createAccount()
        }

    }

    private fun createAccount() {
        progressDialog.setTitle("Creating Account")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                updateUserInfo()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to create account deu to ${e}",
                    Toast.LENGTH_LONG).show()
            }
    }

    private fun updateUserInfo() {
        //user real time firebase to save user info
        progressDialog.setMessage("Saving user info...")


        //timestamp
        val timestamp = System.currentTimeMillis()
        val uid = firebaseAuth.uid // get the registered user ID

        //set up data to add to the database
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["username"] = username
        hashMap["email"] = email
        hashMap["userType"] = "user"
        hashMap["profileImage"] = ""
        hashMap["timestamp"] = timestamp

        //set data to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Account created...",
                    Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, UserDashboardActivity::class.java))
                finish()

            }
            .addOnFailureListener {  e ->
                Toast.makeText(this, "Failed to create account deu to ${e}",
                    Toast.LENGTH_LONG).show()
            }
    }


}