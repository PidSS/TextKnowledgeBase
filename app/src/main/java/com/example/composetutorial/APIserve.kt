
import com.example.composetutorial.RetrofitClient
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class LoginRequest(val name: String, val password: String)
data class RegisterRequest(val name: String, val password: String)
data class User(val id: Int, val name: String, val avatar: String)
data class RegisterResponse(val success: Boolean, val message: String)
data class UpdateFavoriteRequest(val entryId: Int, val isFavorite: Boolean)
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
    val content: String,
    var isFavorite: Boolean = true,
)

data class Collectionn(
    val id: Int,
    val name: String,
    val introduction: String
)

data class ProfileResponse(
    val id: Int,
    val name: String,
    val avatar: String,
    val collections: List<Collectionn>,
    val admin: Boolean,
    val feedbacks: List<Any>
)



interface ApiService {
    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("user/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("entry/list")
    suspend fun getEntries(): List<Entry>

    @POST("updateFavorite")
    suspend fun updateFavorite(@Body request: UpdateFavoriteRequest): ProfileResponse

    @POST("updateFavorite")
    suspend fun updateFavoriteAndGetProfile(userViewModel: UserViewModel): ProfileResponse {
        val updateRequest = UpdateFavoriteRequest(entryId = 1, isFavorite = true) // 根据实际情况设置 entryId 和 isFavorite
        return RetrofitClient.instance.updateFavorite(updateRequest)
    }

    @GET("user/profile") // 根据您的实际接口路径修改
    suspend fun getProfile(@Header("Authorization") token: String): ProfileResponse // 这里使用 suspend 函数以支持协程


}


