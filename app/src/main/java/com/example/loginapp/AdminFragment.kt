package com.example.loginapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.loginapp.databinding.FragmentAdminBinding

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Buat masa ni, semua baris bawa ke skrin Admin Dashboard sedia ada
        // (Urus Pengumuman, Role, App, User semua ada dalam satu skrin tu)
        binding.rowAdminAnnouncement.setOnClickListener { openAdminDashboard() }
        binding.rowAdminRole.setOnClickListener { openAdminDashboard() }
        binding.rowAdminApp.setOnClickListener { openAdminDashboard() }
        binding.rowAdminUser.setOnClickListener { openAdminDashboard() }
    }

    private fun openAdminDashboard() {
        startActivity(Intent(requireContext(), AdminDashboardActivity::class.java))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
