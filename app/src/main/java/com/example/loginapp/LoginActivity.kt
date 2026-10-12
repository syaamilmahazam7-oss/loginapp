package com.example.loginapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.loginapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        loadAnnouncementTicker()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Sila isi email & password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    routeByRole(result.user?.uid)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message ?: "Login gagal", Toast.LENGTH_SHORT).show()
                }
        }

        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loadAnnouncementTicker() {
        db.collection("settings").document("announcement").get()
            .addOnSuccessListener { doc ->
                val text = doc.getString("text")
                if (!text.isNullOrBlank()) {
                    binding.tvAnnouncementTicker.text = text
                }
                binding.tvAnnouncementTicker.isSelected = true
            }
            .addOnFailureListener {
                binding.tvAnnouncementTicker.isSelected = true
            }
    }

    override fun onStart() {
        super.onStart()
        // Auto-login kalau session masih sah
        auth.currentUser?.let { routeByRole(it.uid) }
    }

    private fun routeByRole(uid: String?) {
        if (uid == null) return
        // Admin dan user biasa sama-sama masuk Dashboard (5 tab biasa).
        // Kalau role == admin, tab ke-6 "Admin" akan muncul automatik dalam Dashboard.
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
