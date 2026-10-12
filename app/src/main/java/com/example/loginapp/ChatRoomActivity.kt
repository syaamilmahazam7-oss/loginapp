package com.example.loginapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginapp.adapters.MessageAdapter
import com.example.loginapp.databinding.ActivityChatRoomBinding
import com.example.loginapp.models.ChatMessage
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ChatRoomActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CHAT_ID = "extra_chat_id"
        const val EXTRA_MEMBER_CODE = "extra_member_code"
    }

    private lateinit var binding: ActivityChatRoomBinding
    private val db = FirebaseFirestore.getInstance()
    private var messagesListener: ListenerRegistration? = null
    private lateinit var chatId: String
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatId = intent.getStringExtra(EXTRA_CHAT_ID) ?: run { finish(); return }
        binding.tvChatRoomTitle.text = intent.getStringExtra(EXTRA_MEMBER_CODE) ?: "Chat"

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: run { finish(); return }
        adapter = MessageAdapter(uid)
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        binding.rvMessages.adapter = adapter

        binding.btnSendMessage.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(uid, text)
                binding.etMessage.text.clear()
            }
        }

        listenMessages()
    }

    private fun listenMessages() {
        messagesListener = db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener
                val messages = snapshot.documents.mapNotNull { it.toObject(ChatMessage::class.java) }
                adapter.submitList(messages)
                binding.rvMessages.scrollToPosition(maxOf(0, messages.size - 1))
            }
    }

    private fun sendMessage(uid: String, text: String) {
        val chatRef = db.collection("chats").document(chatId)
        val message = hashMapOf(
            "senderUid" to uid,
            "text" to text,
            "timestamp" to Timestamp.now()
        )
        chatRef.collection("messages").add(message)
        chatRef.update(
            mapOf(
                "lastMessage" to text,
                "updatedAt" to Timestamp.now()
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        messagesListener?.remove()
    }
}
