package com.example.womensafety.Auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.womensafety.R
import com.example.womensafety.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()


        binding.btnLogin.setOnClickListener{
            startActivity(Intent(this@Register,Login::class.java))
            finishAffinity()
        }

        binding.btnSignup.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if ( Validate(email,password))
            {
                CreateUser(email,password)
            }
        }
    }

    private fun CreateUser( email: String, password: String) {

        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{account->
                if (account.isSuccessful)
                {
                   Toast.makeText(this@Register,"Account Created Successful",Toast.LENGTH_LONG).show()
                }
                else
                {
                    Toast.makeText(this@Register,"Failed to create account ${account.exception!!.message}",Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun Validate( email: String, password: String ) : Boolean {


        if (email.isEmpty())
        {
            binding.etEmail.error = "Email should not be empty"
            return false
        }
        if (password.isEmpty())
        {
            binding.etPassword.error = "Password should not be empty"
            return false
        }

        return true
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}