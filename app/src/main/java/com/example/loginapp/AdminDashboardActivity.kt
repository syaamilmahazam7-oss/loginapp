package com.example.loginapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.loginapp.databinding.ActivityAdminDashboardBinding

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private val db = FirebaseFirestore.getInstance()
    private var roleNames = mutableListOf("user")
    private var cachedUsers: List<User> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvRoles.layoutManager = LinearLayoutManager(this)
        binding.rvApps.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvTutorialsAdmin.layoutManager = LinearLayoutManager(this)
        binding.rvAnnouncementsAdmin.layoutManager = LinearLayoutManager(this)

        setupAnnouncement()
        setupRoles()
        setupApps()
        setupTutorials()
        setupAnnouncementPosts()
        setupUsers()
    }

    private fun setupTutorials() {
        db.collection("tutorials")
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener
                val tutorials = snapshot.documents.map { doc ->
                    TutorialItemAdmin(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        videoUrl = doc.getString("videoUrl") ?: "",
                        order = doc.getLong("order") ?: 0
                    )
                }
                binding.rvTutorialsAdmin.adapter = TutorialAdminAdapter(tutorials)
            }

        binding.btnAddTutorial.setOnClickListener {
            val title = binding.etTutorialTitle.text.toString().trim()
            val description = binding.etTutorialDescription.text.toString().trim()
            val videoUrl = binding.etTutorialVideoUrl.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Tajuk tutorial wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tutorial = mapOf(
                "title" to title,
                "description" to description,
                "videoUrl" to videoUrl,
                "order" to System.currentTimeMillis()
            )
            db.collection("tutorials").add(tutorial)
                .addOnSuccessListener {
                    binding.etTutorialTitle.text.clear()
                    binding.etTutorialDescription.text.clear()
                    binding.etTutorialVideoUrl.text.clear()
                    Toast.makeText(this, "Tutorial ditambah", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupAnnouncementPosts() {
        db.collection("announcements")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener
                val posts = snapshot.documents.map { doc ->
                    AnnouncementPostAdmin(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        body = doc.getString("body") ?: "",
                        timestamp = doc.getTimestamp("timestamp")
                    )
                }
                binding.rvAnnouncementsAdmin.adapter = AnnouncementPostAdminAdapter(posts)
            }

        binding.btnAddAnnouncementPost.setOnClickListener {
            val title = binding.etAnnouncementPostTitle.text.toString().trim()
            val body = binding.etAnnouncementPostBody.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Tajuk pengumuman wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val post = mapOf(
                "title" to title,
                "body" to body,
                "timestamp" to Timestamp.now()
            )
            db.collection("announcements").add(post)
                .addOnSuccessListener {
                    binding.etAnnouncementPostTitle.text.clear()
                    binding.etAnnouncementPostBody.text.clear()
                    Toast.makeText(this, "Pengumuman ditambah", Toast.LENGTH_SHORT).show()
                }
        }
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

            // Role baru/padam kena refresh dropdown dalam Senarai User jugak,
            // bukan tunggu collection "users" berubah.
            refreshUserAdapter()
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

            cachedUsers = snapshot.documents.map { doc ->
                User(
                    uid = doc.id,
                    name = doc.getString("name") ?: "",
                    email = doc.getString("email") ?: "",
                    role = doc.getString("role") ?: "user"
                )
            }
            binding.tvCount.text = "Senarai User (${cachedUsers.size})"
            refreshUserAdapter()
        }
    }

    private fun refreshUserAdapter() {
        binding.rvUsers.adapter = UserAdapter(cachedUsers, roleNames)
    }
}
