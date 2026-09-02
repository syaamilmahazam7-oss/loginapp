package com.example.loginapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.example.loginapp.databinding.ActivityAdminDashboardBinding

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private val db = FirebaseFirestore.getInstance()
    private var roleNames = mutableListOf("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvRoles.layoutManager = LinearLayoutManager(this)
        binding.rvApps.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.layoutManager = LinearLayoutManager(this)

        setupAnnouncement()
        setupRoles()
        setupApps()
        setupUsers()
    }

    private fun setupAnnouncement() {
        db.collection("settings").document("announcement").get()
            .addOnSuccessListener { doc ->
                binding.etAnnouncement.setText(doc.getString("text") ?: "")
            }

        binding.btnUpdateAnnouncement.setOnClickListener {
            val text = binding.etAnnouncement.text.toString()
            db.collection("settings").document("announcement")
                .set(mapOf("text" to text, "timestamp" to System.currentTimeMillis()))
                .addOnSuccessListener {
                    Toast.makeText(this, "Announcement dikemaskini", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupRoles() {
        db.collection("roles").addSnapshotListener { snapshot, _ ->
            if (snapshot == null) return@addSnapshotListener
            val roles = snapshot.documents.map { doc ->
                Role(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    order = (doc.getLong("order") ?: 0).toInt()
                )
            }
            binding.rvRoles.adapter = RoleAdapter(roles)

            roleNames = (listOf("user") + roles.map { it.name }).distinct().toMutableList()
            val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roleNames)
            binding.spinnerAppRole.adapter = spinnerAdapter
        }

        binding.btnAddRole.setOnClickListener {
            val name = binding.etNewRole.text.toString().trim()
            if (name.isEmpty()) return@setOnClickListener
            db.collection("roles").add(mapOf("name" to name, "order" to System.currentTimeMillis()))
            binding.etNewRole.text.clear()
        }
    }

    private fun setupApps() {
        db.collection("apps").addSnapshotListener { snapshot, _ ->
            if (snapshot == null) return@addSnapshotListener
            val apps = snapshot.documents.map { doc ->
                AppItem(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    packageName = doc.getString("packageName") ?: "",
                    description = doc.getString("description") ?: "",
                    downloadUrl = doc.getString("downloadUrl") ?: "",
                    videoUrl = doc.getString("videoUrl") ?: "",
                    requiredRole = doc.getString("requiredRole") ?: "user"
                )
            }
            binding.rvApps.adapter = AppAdapter(apps)
        }

        binding.btnAddApp.setOnClickListener {
            val name = binding.etAppName.text.toString().trim()
            val packageName = binding.etAppPackage.text.toString().trim()
            val description = binding.etAppDescription.text.toString().trim()
            val downloadUrl = binding.etAppDownloadUrl.text.toString().trim()
            val videoUrl = binding.etAppVideoUrl.text.toString().trim()
            val requiredRole = binding.spinnerAppRole.selectedItem?.toString() ?: "user"

            if (name.isEmpty() || packageName.isEmpty()) {
                Toast.makeText(this, "Nama & package name wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val app = mapOf(
                "name" to name,
                "packageName" to packageName,
                "description" to description,
                "downloadUrl" to downloadUrl,
                "videoUrl" to videoUrl,
                "requiredRole" to requiredRole
            )
            db.collection("apps").add(app)
                .addOnSuccessListener {
                    binding.etAppName.text.clear()
                    binding.etAppPackage.text.clear()
                    binding.etAppDescription.text.clear()
                    binding.etAppDownloadUrl.text.clear()
                    binding.etAppVideoUrl.text.clear()
                    Toast.makeText(this, "App ditambah", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupUsers() {
        db.collection("users").addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener

            val users = snapshot.documents.map { doc ->
                User(
                    uid = doc.id,
                    name = doc.getString("name") ?: "",
                    email = doc.getString("email") ?: "",
                    role = doc.getString("role") ?: "user"
                )
            }
            binding.tvCount.text = "Senarai User (${users.size})"
            binding.rvUsers.adapter = UserAdapter(users)
        }
    }
}
