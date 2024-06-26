
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Response

class UserViewModel : ViewModel() {
    var user: User? by mutableStateOf(null)
        private set

    fun login(name: String, password: String) {
        viewModelScope.launch {
            try {
                val response: Response<User> = RetrofitClient.instance.login(LoginRequest(name, password))
                if (response.isSuccessful) {
                    user = response.body()
                } else {
                    // 处理登录失败
                }
            } catch (e: Exception) {
                // 处理异常
            }
        }
    }

    fun register(name: String, password: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.register(RegisterRequest(name, password))
                if (response.success) {
                    // 注册成功，处理成功逻辑
                    // 例如，更新用户信息
                    // user = response.user // 假设 RegisterResponse 包含 user 信息
                } else {
                    // 处理注册失败
                    var errorMessage = response.message
                }
            } catch (e: Exception) {
                // 处理异常
                var errorMessage = "注册失败：${e.message}"
            }
        }
    }
}
