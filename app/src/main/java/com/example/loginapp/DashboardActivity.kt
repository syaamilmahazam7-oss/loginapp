package com.example.loginapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.loginapp.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(ItemFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_item -> ItemFragment()
                R.id.nav_tutorial -> TutorialFragment()
                R.id.nav_chat -> ChatFragment()
                R.id.nav_announcement -> AnnouncementFragment()
                R.id.nav_setting -> SettingFragment()
                R.id.nav_admin -> AdminFragment()
                else -> ItemFragment()
            }
            loadFragment(fragment)
            true
        }

        checkAdminRole()
    }

    private fun checkAdminRole() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role") ?: "user"
                if (role == "admin" && binding.bottomNav.menu.findItem(R.id.nav_admin) == null) {
                    // BottomNavigationView max 5 item, so Admin ditambah secara
                    // programatik hanya bila role disahkan admin (bukan dalam XML).
                    binding.bottomNav.menu.add(0, R.id.nav_admin, 5, "Admin")
                        .setIcon(R.drawable.ic_nav_admin)
                }
            }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
