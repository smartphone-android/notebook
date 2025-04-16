package hku.cs.notebook.ui.chat

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
