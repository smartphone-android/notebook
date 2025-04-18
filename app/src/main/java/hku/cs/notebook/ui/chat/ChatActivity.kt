package hku.cs.notebook.ui.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hku.cs.notebook.R
import hku.cs.notebook.databinding.ActivityChatBinding

class ChatActivity: AppCompatActivity() {
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 启用返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 设置RecyclerView和适配器
        chatAdapter = ChatAdapter()
        binding.rvChatMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true  // 使新消息显示在底部
            }
            adapter = chatAdapter
        }

        // 观察数据变化
        viewModel.messages.observe(this) { messages ->
            chatAdapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.rvChatMessages.smoothScrollToPosition(messages.size - 1)
            }
        }

        // 观察加载状态
        viewModel.loading.observe(this) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSend.isEnabled = !isLoading
        }

        // 观察错误信息
        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        // 发送按钮点击事件
        binding.btnSend.setOnClickListener {
            val question = binding.etQuestion.text.toString().trim()
            if (question.isNotEmpty()) {
                viewModel.sendQuestion(question)
                binding.etQuestion.setText("")  // 清空输入框
            }
        }


    }
    // 处理返回按钮点击事件
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // 结束当前活动，返回上一页
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}