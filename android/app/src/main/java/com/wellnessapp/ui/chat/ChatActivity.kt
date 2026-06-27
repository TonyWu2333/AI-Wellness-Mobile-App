package com.wellnessapp.ui.chat

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wellnessapp.data.api.RetrofitClient
import com.wellnessapp.data.model.ChatRequest
import com.wellnessapp.databinding.ActivityChatBinding
import kotlinx.coroutines.launch

/**
 * Chat screen — users can chat with WellBot, the wellness AI assistant.
 *
 * @author WellnessApp Team
 */
class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = ChatMessageAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.btnSend.setOnClickListener { sendMessage() }
        binding.etMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else {
                false
            }
        }

        loadHistory()
    }

    private fun sendMessage() {
        val message = binding.etMessage.text.toString().trim()
        if (message.isEmpty()) return

        // Show user message immediately
        adapter.addMessage(ChatMessageItem.User(message))
        binding.etMessage.text?.clear()
        binding.recyclerView.scrollToPosition(adapter.itemCount - 1)

        showLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.sendChatMessage(
                    ChatRequest(message)
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    val reply = response.body()!!.data!!.reply
                    adapter.addMessage(ChatMessageItem.Bot(reply))
                    binding.recyclerView.scrollToPosition(adapter.itemCount - 1)
                    binding.tvEmpty.visibility = View.GONE
                } else {
                    adapter.addMessage(
                        ChatMessageItem.Bot("Sorry, I couldn't process that. Please try again.")
                    )
                }
            } catch (e: Exception) {
                adapter.addMessage(
                    ChatMessageItem.Bot("Connection error. Please check your network and try again.")
                )
            } finally {
                showLoading(false)
            }
        }
    }

    private fun loadHistory() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getChatHistory()
                if (response.isSuccessful && response.body()?.success == true) {
                    val history = response.body()!!.data ?: emptyList()
                    history.forEach { msg ->
                        adapter.addMessage(ChatMessageItem.User(msg.userMessage))
                        adapter.addMessage(ChatMessageItem.Bot(msg.botResponse))
                    }
                    if (history.isEmpty()) {
                        binding.tvEmpty.visibility = View.VISIBLE
                    }
                }
            } catch (_: Exception) {
                binding.tvEmpty.visibility = View.VISIBLE
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSend.isEnabled = !loading
    }
}

/**
 * Sealed class for chat message types in the RecyclerView.
 */
sealed class ChatMessageItem {
    data class User(val message: String) : ChatMessageItem()
    data class Bot(val message: String) : ChatMessageItem()
}
