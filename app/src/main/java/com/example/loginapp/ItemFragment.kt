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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Nota: tab Item memaparkan collection "apps" (sama data yang admin urus
// dalam Admin Dashboard -> "Urus Senarai App (Item)"), bukan collection "items".
//
// Enforcement requiredRole: setiap app HANYA kelihatan kepada user yang rolenya
// SAMA PERSIS dengan requiredRole app tu (admin nampak semua app, tanpa kira role).
// Contoh: app "Belalang" ditanda requiredRole="clicker" -> cuma user dengan
// role="clicker" nampak app tu dalam tab Item. User role lain (termasuk "user"
// biasa) TAK nampak app tu langsung -- bukan sekadar locked/disabled, terus
// tak masuk dalam senarai.

class ItemFragment : Fragment() {

    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: ItemAdapter
    private var currentUserRole = "user"

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

        loadUserRoleThenItems()
    }

    private fun loadUserRoleThenItems() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            loadItems()
            return
        }
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (_binding == null) return@addOnSuccessListener
                currentUserRole = doc.getString("role") ?: "user"
                loadItems()
            }
            .addOnFailureListener {
                if (_binding == null) return@addOnFailureListener
                loadItems()
            }
    }

    private fun loadItems() {
        db.collection("apps").get()
            .addOnSuccessListener { snapshot ->
                if (_binding == null) return@addOnSuccessListener
                val items = snapshot.documents.mapNotNull { doc ->
                    val requiredRole = doc.getString("requiredRole") ?: "user"
                    val isAllowed = currentUserRole == "admin" || requiredRole == currentUserRole
                    if (!isAllowed) return@mapNotNull null
                    ItemModel(
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        downloadUrl = doc.getString("downloadUrl") ?: "",
                        badge = if (requiredRole != "user") requiredRole.uppercase() else "",
                        id = doc.id
                    )
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
