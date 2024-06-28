import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    // 登录状态
    private var _isLoggedIn = mutableStateOf(false)
    val isLoggedIn get() = _isLoggedIn.value

    // 用户名
    private var _username = mutableStateOf("")
    val username get() = _username.value

    // 登录操作
    fun login(username: String) {
        viewModelScope.launch {
            _username.value = username
            _isLoggedIn.value = true
        }
    }

    // 退出登录
    fun logout() {
        viewModelScope.launch {
            _username.value = ""
            _isLoggedIn.value = false
        }
    }
}
