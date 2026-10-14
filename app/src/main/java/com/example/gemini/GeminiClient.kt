package com.example.gemini

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Low-latency responses using gemini-3.1-flash-lite
     */
    suspend fun getLowLatencyResponse(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API key is not configured. Please set GEMINI_API_KEY in the Secrets panel."
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent?key=$apiKey"

        val requestJson = JSONObject().apply {
            val contents = JSONArray().apply {
                val userContent = JSONObject().apply {
                    put("role", "user")
                    val parts = JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    }
                    put("parts", parts)
                }
                put(userContent)
            }
            put("contents", contents)
        }

        executeGeminiRequest(url, requestJson.toString())
    }

    /**
     * Video content analysis using gemini-3.1-pro-preview
     */
    suspend fun analyzeVideoContent(prompt: String, videoBase64: String? = null, mimeType: String = "video/mp4"): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API key is not configured. Please set GEMINI_API_KEY in the Secrets panel."
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-pro-preview:generateContent?key=$apiKey"

        val requestJson = JSONObject().apply {
            val contents = JSONArray().apply {
                val userContent = JSONObject().apply {
                    put("role", "user")
                    val parts = JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                        if (!videoBase64.isNullOrBlank()) {
                            put(JSONObject().apply {
                                val inlineData = JSONObject().apply {
                                    put("mimeType", mimeType)
                                    put("data", videoBase64)
                                }
                                put("inlineData", inlineData)
                            })
                        }
                    }
                    put("parts", parts)
                }
                put(userContent)
            }
            put("contents", contents)
        }

        executeGeminiRequest(url, requestJson.toString())
    }

    /**
     * High thinking mode using gemini-3.1-pro-preview with thinkingLevel HIGH.
     * Note: Do NOT set maxOutputTokens per user constraint.
     */
    suspend fun getHighThinkingResponse(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API key is not configured. Please set GEMINI_API_KEY in the Secrets panel."
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-pro-preview:generateContent?key=$apiKey"

        val requestJson = JSONObject().apply {
            val contents = JSONArray().apply {
                val userContent = JSONObject().apply {
                    put("role", "user")
                    val parts = JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    }
                    put("parts", parts)
                }
                put(userContent)
            }
            put("contents", contents)

            // Thinking mode config: thinkingLevel HIGH, NO maxOutputTokens
            val generationConfig = JSONObject().apply {
                val thinkingConfig = JSONObject().apply {
                    put("thinkingLevel", "HIGH")
                }
                put("thinkingConfig", thinkingConfig)
            }
            put("generationConfig", generationConfig)
        }

        executeGeminiRequest(url, requestJson.toString())
    }

    private fun executeGeminiRequest(url: String, jsonBody: String): String {
        return try {
            val body = jsonBody.toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                val responseString = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    Log.e("GeminiClient", "Gemini HTTP error ${response.code}: $responseString")
                    val errorMsg = try {
                        JSONObject(responseString).optJSONObject("error")?.optString("message") ?: responseString
                    } catch (e: Exception) {
                        responseString
                    }
                    return "Error (${response.code}): $errorMsg"
                }

                val json = JSONObject(responseString)
                val candidates = json.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val textBuilder = StringBuilder()
                        for (i in 0 until parts.length()) {
                            val part = parts.getJSONObject(i)
                            if (part.has("text")) {
                                textBuilder.append(part.getString("text"))
                            }
                        }
                        return textBuilder.toString().ifEmpty { "No textual content returned." }
                    }
                }
                "No response text received from model."
            }
        } catch (e: Exception) {
            Log.e("GeminiClient", "Exception calling Gemini", e)
            "Connection failed: ${e.message}"
        }
    }
}
