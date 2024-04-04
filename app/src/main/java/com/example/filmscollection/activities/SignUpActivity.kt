package com.example.filmscollection.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.filmscollection.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.sign_up_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val linkToLogin: TextView = findViewById(R.id.linkToSignIn)
        linkToLogin.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        val btn = findViewById<Button>(R.id.registerButton)
        btn.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailEdit).text.toString()
            val password = findViewById<EditText>(R.id.passwordEdit).text.toString()
            val retypePassword = findViewById<EditText>(R.id.retypePasswordEdit).text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() && retypePassword.isNotEmpty()) {
                if (password == retypePassword) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { signUpTask ->
                            if (signUpTask.isSuccessful) {
                                val uid = signUpTask.result?.user?.uid
                                val db = FirebaseFirestore.getInstance()
                                val user = hashMapOf(
                                    "email" to email,
                                    "favorites" to listOf<Int>()
                                )
                                db.collection("users")
                                    .document(email)
                                    .set(user)
                                    .addOnSuccessListener {
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to sign up: ${signUpTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty fields not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}