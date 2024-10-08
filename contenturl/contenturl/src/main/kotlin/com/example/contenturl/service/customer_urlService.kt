package com.example.contenturl.service
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.contenturl.repository.customer_urlsRepository
import jakarta.annotation.PostConstruct
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Service

@Service
@EnableScheduling
class customer_urlService @Autowired constructor(
    private val customerUrlsrepository: customer_urlsRepository
) {
    private var userId: Long = 1
    @PostConstruct
    fun init() {
        GlobalScope.launch {
            while (true) {
                fetchPostsForUser(userId)
                delay(5 * 60 * 1000)
            }
        }
    }

    suspend fun fetchPostsForUser(userId: Long) {
        val user = customerUrlsrepository.findById(userId)
        user?.let {
            val client = OkHttpClient()

            val mediaType = "application/json".toMediaType()
            val body = """
        {
            "profile_url": "${it.get().customer_url}",
            "work_platform_id": "9bb8913b-ddd9-430b-a66a-d74d846e6c66",
            "content_type": "REELS",
            "offset": 0,
            "limit": 1
        }
    """.trimIndent().toRequestBody(mediaType)
//            println("Fetching posts for the ${it.get().customer_url}")
//            delay(1000)

            val request = Request.Builder()
                .url("https://api.staging.insightiq.ai/v1/social/creators/contents/fetch")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Basic MDBlNThmNDUtN2RjNi00NzA3LWJmZjktZmRiYmVjMzIzNGVmOjE0YTg0NzM1LWQwYTItNGIyZC1hZDMwLTk2MjEyMDVlYzhhMQ==")
                .build()
            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    println("Response: ${response.body?.string()}")
                } else {
                    println("Request failed: ${response.code} ${response.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setUserId(newUserId: Long) {
        userId = newUserId
    }

}



