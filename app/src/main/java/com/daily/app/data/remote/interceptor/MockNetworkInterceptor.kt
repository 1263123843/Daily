package com.daily.app.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Mock 网络拦截器 — 拦截所有 HTTP 请求并返回模拟 JSON 响应.
 *
 * 在 MOCK_MODE 下添加到 OkHttp 链中，无需改动任何上游代码（UseCase / ViewModel / Repository）。
 * 所有请求都会命中此拦截器并直接返回，不会发起真实网络请求。
 */
class MockNetworkInterceptor : Interceptor {

    private var nextCheckinId = 1000L
    private val mockContacts = mutableListOf<MockContact>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val body = request.body?.string()

        return mockResponse(request, url, body)
    }

    private fun mockResponse(request: okhttp3.Request, url: String, body: String?): Response {
        val mediaType = "application/json".toMediaType()

        return when {
            // POST /api/v1/checkin
            url.endsWith("/checkin") && request.method == "POST" -> {
                val now = Instant.now().truncatedTo(ChronoUnit.SECONDS)
                successResponse(
                    request,
                    """{
                        "checkin_id": ${nextCheckinId++},
                        "checkin_time": "${now.toString()}",
                        "status": "ok"
                    }"""
                )
            }

            // GET /api/v1/users/me/status
            url.endsWith("/users/me/status") -> {
                val now = Instant.now()
                successResponse(
                    request,
                    """{
                        "user_id": "local_user",
                        "nickname": "Demo",
                        "status": "normal",
                        "last_checkin_time": "${now.minus(5, ChronoUnit.MINUTES)}",
                        "last_checkin_ago_minutes": 5,
                        "consecutive_days": 7,
                        "emergency_contacts_count": ${mockContacts.size}
                    }"""
                )
            }

            // GET /api/v1/users/me/checkins
            url.contains("/users/me/checkins") -> {
                val now = Instant.now()
                val items = (0 until 7).map { daysAgo ->
                    val id = 900L - daysAgo
                    val source = if (daysAgo % 2 == 0) "auto" else "manual"
                    """{"checkin_id": $id, "checkin_time": "${now.minus(daysAgo.toLong(), ChronoUnit.DAYS)}", "status": "$source"}"""
                }.joinToString(", ")
                successResponse(
                    request,
                    """{
                        "items": [$items],
                        "total": 7,
                        "page": 1,
                        "page_size": 20,
                        "total_pages": 1
                    }"""
                )
            }

            // POST /api/v1/contacts
            url.endsWith("/contacts") && request.method == "POST" -> {
                val name = extractJsonString(body, "name") ?: "Mock"
                val relationship = extractJsonString(body, "relationship") ?: "parent"
                val phone = extractJsonString(body, "phone_encrypted") ?: "138****5678"
                val id = (mockContacts.size + 1).toLong()
                val maskedPhone = if (phone.length >= 7) {
                    "${phone.take(3)}****${phone.takeLast(4)}"
                } else {
                    phone
                }
                mockContacts.add(MockContact(id, name, relationship, phone, true))
                successResponse(
                    request,
                    """{
                        "contact_id": $id,
                        "name": "$name",
                        "relationship": "$relationship",
                        "phone_masked": "$maskedPhone",
                        "is_verified": true
                    }"""
                )
            }

            // DELETE /api/v1/contacts/{id}
            url.contains("/contacts/") && request.method == "DELETE" -> {
                val idStr = url.substringAfterLast("/").trim()
                val id = idStr.toLongOrNull() ?: 0L
                mockContacts.removeAll { it.id == id }
                successResponse(request, """{}""")
            }

            // POST /api/v1/sms/send-code
            url.endsWith("/sms/send-code") -> {
                successResponse(request, """{}""")
            }

            // POST /api/v1/sms/verify
            url.endsWith("/sms/verify") -> {
                successResponse(
                    request,
                    """{"verified": true}"""
                )
            }

            else -> {
                // Fallback: 返回空成功响应
                successResponse(request, """{}""")
            }
        }
    }

    private fun successResponse(request: okhttp3.Request, json: String): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(json.trimIndent().toResponseBody("application/json".toMediaType()))
            .addHeader("Content-Type", "application/json")
            .build()
    }

    private fun extractJsonString(json: String?, key: String): String? {
        if (json.isNullOrEmpty()) return null
        // Simple extraction — good enough for mock mode
        val pattern = "\"$key\"\\s*:\\s*\"([^\"]*)\""
        return Regex(pattern).find(json)?.groupValues?.get(1)
    }

    private data class MockContact(
        val id: Long,
        val name: String,
        val relationship: String,
        val phone: String,
        val isVerified: Boolean
    )
}
