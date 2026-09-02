package com.example.loginapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.loginapp.databinding.ItemUserBinding

class UserAdapter(
    private val users: List<User>,
    private val roleNames: List<String> = listOf("user", "Pro")
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    inner class UserViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.binding.tvName.text = user.name.ifEmpty { "(tiada nama)" }
        holder.binding.tvEmail.text = user.email

        val options = if (user.role == "admin") roleNames + "admin" else roleNames
        val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_dropdown_item, options)
        holder.binding.spinnerRole.adapter = adapter

        val currentIndex = options.indexOf(user.role)
        if (currentIndex >= 0) holder.binding.spinnerRole.setSelection(currentIndex, false)

        holder.binding.spinnerRole.onItemSelectedListener = null
        holder.binding.spinnerRole.post {
            holder.binding.spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, pos: Int, id: Long) {
                    val newRole = options[pos]
                    if (newRole != user.role) {
                        db.collection("users").document(user.uid).update("role", newRole)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    override fun getItemCount() = users.size
}
