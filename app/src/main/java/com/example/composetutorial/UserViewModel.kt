
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private var _isLoggedIn = MutableLiveData(false)
    val isLoggedIn:LiveData<Boolean> get() = _isLoggedIn

    private var _username = MutableLiveData("")
    val username:LiveData<String> get() = _username

    private var _token = MutableLiveData("")
    val token:LiveData<String> get() = _token
    fun login(username: String,token:String) {
        viewModelScope.launch {
            _username.value = username
            println("登录成功，Usernamee: ${_username.value}")
            _isLoggedIn.value = true
            _token.value=token
        }
    }




    fun logout() {
        viewModelScope.launch {
            _username.value = ""
            _isLoggedIn.value = false
        }
    }
}
