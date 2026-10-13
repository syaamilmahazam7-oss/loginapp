package com.example.loginapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.loginapp.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(ItemFragment())
            // Minta akses storan SELEPAS user dah masuk Dashboard (bukan masa app baru dibuka),
            // supaya akaun yang login boleh dikongsi dengan app lain (Belalang dll).
            AccountShare.promptForAccess(this)
        }

        // Nota: BottomNavigationView ada HARD LIMIT 5 item (akan crash
        // IllegalArgumentException kalau cuba tambah ke-6, walaupun programatik).
        // Sebab tu akses Admin diletak dalam tab Setting (SettingFragment), bukan
        // sebagai tab berasingan di sini.
        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_item -> ItemFragment()
                R.id.nav_tutorial -> TutorialFragment()
                R.id.nav_chat -> ChatFragment()
                R.id.nav_announcement -> AnnouncementFragment()
                R.id.nav_setting -> SettingFragment()
                else -> ItemFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        // Tulis/kemas kini fail akaun kongsi. Senyap kalau belum ada akses storan;
        // lepas user benarkan di Settings dan kembali ke app, ia terus tertulis di sini.
        AccountShare.write(this)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
