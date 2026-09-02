package com.example.loginapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.loginapp.databinding.ItemRoleBinding

class RoleAdapter(private val roles: List<Role>) :
    RecyclerView.Adapter<RoleAdapter.RoleViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    inner class RoleViewHolder(val binding: ItemRoleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoleViewHolder {
        val binding = ItemRoleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoleViewHolder, position: Int) {
        val role = roles[position]
        holder.binding.tvRoleName.text = role.name

        holder.binding.btnDeleteRole.setOnClickListener {
            db.collection("roles").document(role.id).delete()
        }
    }

    override fun getItemCount() = roles.size
}
