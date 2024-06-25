
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
                val response: Response<User> = RetrofitClient.instance.register(RegisterRequest(name, password))
                if (response.isSuccessful) {
                    user = response.body()
                } else {
                    // 处理注册失败
                }
            } catch (e: Exception) {
                // 处理异常
            }
        }
    }
}
