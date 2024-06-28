import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private var _isLoggedIn = mutableStateOf(false)
    val isLoggedIn get() = _isLoggedIn.value

    private var _username = mutableStateOf("")
    val username get() = _username.value

    fun login(username: String) {
        viewModelScope.launch {
            _username.value = username
            println("登录成功，Usernamee: $username")
            _isLoggedIn.value = true
        }
    }


    fun logout() {
        viewModelScope.launch {
            _username.value = ""
            _isLoggedIn.value = false
        }
    }
}
