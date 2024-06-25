import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val name: String, val password: String)
data class RegisterRequest(val name: String, val password: String)
data class User(val id: Int, val name: String, val avatar: String)

interface ApiService {
    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): Response<User>

    @POST("user/register")
    suspend fun register(@Body request: RegisterRequest): Response<User>
}
