package com.example.loginapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginapp.adapters.TutorialAdapter
import com.example.loginapp.databinding.FragmentTutorialBinding
import com.example.loginapp.models.TutorialModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TutorialFragment : Fragment() {

    private var _binding: FragmentTutorialBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: TutorialAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTutorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TutorialAdapter { tutorial ->
            if (tutorial.videoUrl.isNotBlank()) {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(tutorial.videoUrl)))
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Pautan tidak sah", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Pautan tutorial belum tersedia", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvTutorials.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTutorials.adapter = adapter

        loadTutorials()
    }

    private fun loadTutorials() {
        db.collection("tutorials")
            .orderBy("order", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                if (_binding == null) return@addOnSuccessListener
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(TutorialModel::class.java)?.apply { id = doc.id }
                }
                adapter.submitList(items)
                binding.tvTutorialEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                if (_binding == null) return@addOnFailureListener
                binding.tvTutorialEmpty.visibility = View.VISIBLE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
