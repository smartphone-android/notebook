package hku.cs.notebook.ui.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import hku.cs.notebook.R

class ChatActivity: AppCompatActivity() {
    private val viewModel: ChatViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        // 启用返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 初始化UI组件
        val etQuestion = findViewById<EditText>(R.id.etQuestion)
        val btnSend = findViewById<Button>(R.id.btnSend)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        // 观察数据变化
        viewModel.loading.observe(this) { isLoading ->
            btnSend.isEnabled = !isLoading
            if (isLoading) {
                tvResult.text = "加载中..."
            }
        }

        viewModel.result.observe(this) { answer ->
            tvResult.text = answer
        }

        // 观察错误信息
        viewModel.error.observe(this) { errorMsg ->
            if (errorMsg != null) {
                tvResult.text = "出现错误：$errorMsg"
            }
        }


        // 按钮点击事件
        btnSend.setOnClickListener {
            val question = etQuestion.text.toString().trim()
            if (question.isNotEmpty()) {
                viewModel.sendQuestion(question)
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