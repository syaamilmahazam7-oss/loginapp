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
import com.example.loginapp.adapters.ItemAdapter
import com.example.loginapp.databinding.FragmentItemBinding
import com.example.loginapp.models.ItemModel
import com.google.firebase.firestore.FirebaseFirestore

class ItemFragment : Fragment() {

    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ItemAdapter { item ->
            if (item.downloadUrl.isNotBlank()) {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.downloadUrl)))
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Pautan tidak sah", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Pautan muat turun belum tersedia", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvItemApps.layoutManager = LinearLayoutManager(requireContext())
        binding.rvItemApps.adapter = adapter

        loadItems()
    }

    private fun loadItems() {
        db.collection("items").get()
            .addOnSuccessListener { snapshot ->
                if (_binding == null) return@addOnSuccessListener
                val items = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ItemModel::class.java)?.apply { id = doc.id }
                }
                adapter.submitList(items)
                binding.tvItemEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                if (_binding == null) return@addOnFailureListener
                binding.tvItemEmpty.visibility = View.VISIBLE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
