package com.example.womensafety.Auth

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.womensafety.Activities.HomeActivity
import com.example.womensafety.R
import com.example.womensafety.databinding.ActivityLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser

        if (user!=null)
        {
            startActivity(Intent(this@Login,HomeActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
               Toast.makeText(this@Login,"Please fill all the credentials",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    Toast.makeText(this@Login,"Login Successful",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@Login, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@Login,"Failed to Login ${task.exception!!.message}",Toast.LENGTH_LONG).show()
                }

            }
        }
        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this@Login, Register::class.java))
            finish()
        }

    }
}
