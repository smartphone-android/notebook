package hku.cs.notebook.ui.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hku.cs.notebook.R
import hku.cs.notebook.database.SQLiteHelper

import hku.cs.notebook.ui.editor.EditorViewModel;

class ChatAdapter : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(ChatDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AI = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isFromUser) VIEW_TYPE_USER else VIEW_TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_user, parent, false)
                UserMessageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_ai, parent, false)
                AiMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is UserMessageViewHolder -> holder.bind(message)
            is AiMessageViewHolder -> holder.bind(message)
        }
    }


    class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUserMessage: TextView = itemView.findViewById(R.id.tvUserMessage)

        fun bind(message: ChatMessage) {
            tvUserMessage.text = message.content
        }
    }

    class AiMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAiMessage: TextView = itemView.findViewById(R.id.tvAiMessage)
        private val copyButton: ImageView = itemView.findViewById(R.id.copyButton)

        fun bind(message: ChatMessage) {
            tvAiMessage.text = message.content

            copyButton.setOnClickListener {
                val context = itemView.context
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("AI Message", tvAiMessage.text.toString())
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show()

                // **直接创建笔记**
                saveNote(context, tvAiMessage.text.toString())
            }
        }

        private fun saveNote(context: Context, noteContent: String) {
            val prefs = context.getSharedPreferences("NotebookPrefs", Context.MODE_PRIVATE)
            val currentUserId = prefs.getString("userId", "-1") ?: "-1"

            // **初始化 SQLiteHelper**
            val sqliteHelper = SQLiteHelper(context)

            // **初始化 EditorViewModel 并传入 SQLiteHelper**
            val editorViewModel = EditorViewModel().apply {
                init(sqliteHelper, null)
            }

            // 获取第一行内容
            val firstLine = noteContent.lines().firstOrNull()?.trim() ?: "未命名AI笔记"
            val noteName = if (firstLine.length > 20) firstLine.substring(0, 20) else firstLine

            // **保存笔记**
            val success = editorViewModel.saveNote(currentUserId, noteContent, noteName)
            if (success) {
                Toast.makeText(context, "笔记已创建", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "创建笔记失败", Toast.LENGTH_SHORT).show()
            }
        }


    }


    class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
