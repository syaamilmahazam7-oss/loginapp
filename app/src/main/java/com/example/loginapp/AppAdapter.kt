package com.example.loginapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.loginapp.databinding.ItemAppBinding

class AppAdapter(private val apps: List<AppItem>) :
    RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    inner class AppViewHolder(val binding: ItemAppBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        holder.binding.tvAppName.text = app.name
        holder.binding.tvAppDescription.text = app.description
        holder.binding.tvAppMeta.text = "${app.packageName} • Perlu ${app.requiredRole}"

        holder.binding.btnDeleteApp.setOnClickListener {
            db.collection("apps").document(app.id).delete()
        }

        holder.binding.btnEditApp.setOnClickListener {
            // Edit akan ditambah kemudian - buat masa ni cuma delete & tambah semula
        }
    }

    override fun getItemCount() = apps.size
}
