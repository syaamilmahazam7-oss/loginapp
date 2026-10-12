package com.example.loginapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.loginapp.databinding.ItemTutorialAdminBinding

class TutorialAdminAdapter(private val tutorials: List<TutorialItemAdmin>) :
    RecyclerView.Adapter<TutorialAdminAdapter.VH>() {

    private val db = FirebaseFirestore.getInstance()

    inner class VH(val binding: ItemTutorialAdminBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemTutorialAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val tutorial = tutorials[position]
        holder.binding.tvTutorialAdminTitle.text = tutorial.title
        holder.binding.tvTutorialAdminDescription.text = tutorial.description

        holder.binding.btnDeleteTutorial.setOnClickListener {
            db.collection("tutorials").document(tutorial.id).delete()
        }
    }

    override fun getItemCount() = tutorials.size
}
