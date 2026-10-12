package com.example.loginapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginapp.adapters.ChatAdapter
import com.example.loginapp.databinding.FragmentChatBinding
import com.example.loginapp.models.ChatSummary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ChatAdapter { chat ->
            val intent = Intent(requireContext(), ChatRoomActivity::class.java)
            intent.putExtra(ChatRoomActivity.EXTRA_CHAT_ID, chat.chatId)
            intent.putExtra(ChatRoomActivity.EXTRA_MEMBER_CODE, chat.memberCode)
            startActivity(intent)
        }
        binding.rvChats.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChats.adapter = adapter

        loadChats()
    }

    private fun loadChats() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            binding.tvChatEmpty.visibility = View.VISIBLE
            return
        }
        db.collection("chats")
            .whereArrayContains("participants", uid)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                if (_binding == null) return@addOnSuccessListener
                val items = snapshot.documents.map { doc ->
                    ChatSummary(
                        chatId = doc.id,
                        memberCode = doc.getString("otherMemberCode") ?: doc.id,
                        lastMessage = doc.getString("lastMessage") ?: "",
                        updatedAt = doc.getTimestamp("updatedAt")
                    )
                }
                adapter.submitList(items)
                binding.tvChatEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener {
                if (_binding == null) return@addOnFailureListener
                binding.tvChatEmpty.visibility = View.VISIBLE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
