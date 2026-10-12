package com.example.loginapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.loginapp.databinding.RowAnnouncementBinding
import com.example.loginapp.databinding.RowChatBinding
import com.example.loginapp.databinding.RowItemBinding
import com.example.loginapp.databinding.RowMessageBinding
import com.example.loginapp.databinding.RowTutorialBinding
import com.example.loginapp.models.AnnouncementModel
import com.example.loginapp.models.ChatMessage
import com.example.loginapp.models.ChatSummary
import com.example.loginapp.models.ItemModel
import com.example.loginapp.models.TutorialModel
import java.text.SimpleDateFormat
import java.util.Locale

class ItemAdapter(
    private val items: MutableList<ItemModel> = mutableListOf(),
    private val onDownloadClick: (ItemModel) -> Unit
) : RecyclerView.Adapter<ItemAdapter.VH>() {

    inner class VH(val binding: RowItemBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newItems: List<ItemModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.tvItemName.text = item.name
        holder.binding.tvItemDescription.text = item.description
        holder.binding.tvItemInitial.text = item.name.trim().take(1).uppercase().ifEmpty { "?" }
        if (item.badge.isNotBlank()) {
            holder.binding.tvItemBadge.visibility = android.view.View.VISIBLE
            holder.binding.tvItemBadge.text = item.badge
        } else {
            holder.binding.tvItemBadge.visibility = android.view.View.GONE
        }
        holder.binding.btnItemDownload.setOnClickListener { onDownloadClick(item) }
    }
}

class TutorialAdapter(
    private val items: MutableList<TutorialModel> = mutableListOf(),
    private val onWatchClick: (TutorialModel) -> Unit
) : RecyclerView.Adapter<TutorialAdapter.VH>() {

    inner class VH(val binding: RowTutorialBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newItems: List<TutorialModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RowTutorialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.tvTutorialOrder.text = String.format(Locale.getDefault(), "%02d", position + 1)
        holder.binding.tvTutorialTitle.text = item.title
        holder.binding.tvTutorialDescription.text = item.description
        holder.binding.btnTutorialWatch.setOnClickListener { onWatchClick(item) }
    }
}

class AnnouncementAdapter(
    private val items: MutableList<AnnouncementModel> = mutableListOf()
) : RecyclerView.Adapter<AnnouncementAdapter.VH>() {

    inner class VH(val binding: RowAnnouncementBinding) : RecyclerView.ViewHolder(binding.root)

    private val dateFormat = SimpleDateFormat("dd MMM", Locale("ms", "MY"))

    fun submitList(newItems: List<AnnouncementModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RowAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.tvAnnouncementTitle.text = item.title
        holder.binding.tvAnnouncementBody.text = item.body
        holder.binding.tvAnnouncementDate.text = item.timestamp?.toDate()?.let { dateFormat.format(it) } ?: ""
    }
}

class ChatAdapter(
    private val items: MutableList<ChatSummary> = mutableListOf(),
    private val onChatClick: (ChatSummary) -> Unit
) : RecyclerView.Adapter<ChatAdapter.VH>() {

    inner class VH(val binding: RowChatBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newItems: List<ChatSummary>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RowChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.tvChatMemberCode.text = item.memberCode
        holder.binding.tvChatInitial.text = item.memberCode.trim().take(1).uppercase().ifEmpty { "#" }
        holder.binding.tvChatLastMessage.text = item.lastMessage.ifBlank { "Tiada mesej lagi" }
        holder.binding.root.setOnClickListener { onChatClick(item) }
    }
}

class MessageAdapter(
    private val currentUid: String,
    private val items: MutableList<ChatMessage> = mutableListOf()
) : RecyclerView.Adapter<MessageAdapter.VH>() {

    inner class VH(val binding: RowMessageBinding) : RecyclerView.ViewHolder(binding.root)

    fun submitList(newItems: List<ChatMessage>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RowMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val message = items[position]
        holder.binding.tvMessageBubble.text = message.text
        val isMine = message.senderUid == currentUid
        val params = holder.binding.tvMessageBubble.layoutParams
        if (params is android.widget.LinearLayout.LayoutParams) {
            params.gravity = if (isMine) android.view.Gravity.END else android.view.Gravity.START
            holder.binding.tvMessageBubble.layoutParams = params
        }
    }
}
