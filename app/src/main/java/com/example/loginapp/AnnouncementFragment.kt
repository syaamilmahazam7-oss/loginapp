package com.example.loginapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginapp.adapters.AnnouncementAdapter
import com.example.loginapp.databinding.FragmentAnnouncementBinding
import com.example.loginapp.models.AnnouncementModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AnnouncementFragment : Fragment() {

    private var _binding: FragmentAnnouncementBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val adapter = AnnouncementAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnnouncementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvAnnouncements.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAnnouncements.adapter = adapter

        loadAnnouncements()
    }

    private fun loadAnnouncements() {
        db.collection("announcements")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                if (_binding == null) return@addOnSuccessListener
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(AnnouncementModel::class.java)?.apply { id = doc.id }
                }
                adapter.submitList(items)
                binding.tvAnnouncementEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                if (_binding == null) return@addOnFailureListener
                binding.tvAnnouncementEmpty.visibility = View.VISIBLE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
