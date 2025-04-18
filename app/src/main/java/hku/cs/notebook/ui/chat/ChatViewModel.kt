package hku.cs.notebook.ui.chat

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.tencentcloudapi.common.profile.ClientProfile
import com.tencentcloudapi.common.profile.HttpProfile
import com.tencentcloudapi.hunyuan.v20230901.HunyuanClient
import com.tencentcloudapi.hunyuan.v20230901.models.ChatCompletionsRequest
import com.tencentcloudapi.hunyuan.v20230901.models.Message
class ChatViewModel(application: Application): AndroidViewModel(application) {
    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: MutableLiveData<String?> = _error

    // 添加新消息
    private fun addMessage(message: ChatMessage) {
        val currentList = _messages.value ?: emptyList()
        _messages.value = currentList + message
    }

    // 发送请求
    fun sendQuestion(question: String) {
        // 添加用户消息到聊天列表
        addMessage(ChatMessage(content = question, isFromUser = true))

        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val answer = withContext(Dispatchers.IO) {
                    sendHunyuanRequest(question)
                }

                // 添加AI回复到聊天列表
                addMessage(ChatMessage(content = answer, isFromUser = false))
            } catch (e: Exception) {
                _error.value = "异常：${e.message}"
                Log.e("ChatViewModel", "发生异常: ${e.stackTraceToString()}")

                // 添加错误消息到聊天列表
                addMessage(ChatMessage(content = "抱歉，出现了错误：${e.message}", isFromUser = false))
            } finally {
                _loading.value = false
            }
        }
    }

    private fun sendHunyuanRequest(question: String): String {
        try {
            // 1. 实例化认证对象
            val secretId = "替换为实际的SecretId"
            val secretKey = "替换为实际的SecretKey"
            val cred = com.tencentcloudapi.common.Credential(secretId, secretKey)

            // 2. 配置网络设置
            val httpProfile = HttpProfile()
            httpProfile.endpoint = "hunyuan.tencentcloudapi.com"

            // 3. 配置客户端
            val clientProfile = ClientProfile()
            clientProfile.httpProfile = httpProfile

            // 4. 创建客户端
            val client = HunyuanClient(cred, "", clientProfile)

            // 5. 构建请求
            val req = ChatCompletionsRequest()
            req.model = "hunyuan-lite"  // 或hunyuan-turbo
            req.stream = false
            // 6. 设置消息
            val message = Message()
            message.role = "user"
            message.content = question
            req.messages = arrayOf(message)

            // 7. 发送请求
            val resp = client.ChatCompletions(req)

            // 8. 处理响应
            val content = resp.choices[0].message.content

            return content
        } catch (e: Exception) {
            Log.e("ChatViewModel", "SDK调用异常：${e.message}")
            throw e
        }
    }


}