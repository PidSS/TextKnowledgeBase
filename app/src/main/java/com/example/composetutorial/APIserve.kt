
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class LoginRequest(val name: String, val password: String)
data class RegisterRequest(val name: String, val password: String)
data class User(val id: Int, val name: String, val avatar: String)
data class RegisterResponse(val success: Boolean, val message: String)
data class LoginResponse(
    val id: Int,
    val name: String,
    val avatar: String,
    val collections: List<Any>, // 假设 collections 是一个列表
    val admin: Boolean,
    val feedbacks: List<Any>, // 假设 feedbacks 是一个列表
    val token: String,
)

data class Entry(
    val id: Int,
    val name: String,
    val introduction: String,
    val content: String
)



interface ApiService {
    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("user/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("entry/list")
    suspend fun getEntries(): List<Entry>

}


