package com.example.loginapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.loginapp.databinding.ItemAnnouncementAdminBinding

class AnnouncementPostAdminAdapter(private val posts: List<AnnouncementPostAdmin>) :
    RecyclerView.Adapter<AnnouncementPostAdminAdapter.VH>() {

    private val db = FirebaseFirestore.getInstance()

    inner class VH(val binding: ItemAnnouncementAdminBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemAnnouncementAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val post = posts[position]
        holder.binding.tvAnnouncementAdminTitle.text = post.title
        holder.binding.tvAnnouncementAdminBody.text = post.body

        holder.binding.btnDeleteAnnouncementPost.setOnClickListener {
            db.collection("announcements").document(post.id).delete()
        }
    }

    override fun getItemCount() = posts.size
}
