package com.viagens.data.repository

import android.util.Log
import com.viagens.BuildConfig
import com.viagens.data.network.Content
import com.viagens.data.network.GeminiApiService
import com.viagens.data.network.GeminiRequest
import com.viagens.data.network.Part
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GeminiRepository {
    private val apiService: GeminiApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(GeminiApiService::class.java)
        
        // Logs de diagnóstico
        Log.d("GeminiConfig", "Base URL: https://generativelanguage.googleapis.com/")
        Log.d("GeminiConfig", "API Key present: ${BuildConfig.GEMINI_API_KEY.isNotEmpty()}")
        if (BuildConfig.GEMINI_API_KEY.isEmpty()) {
            Log.e("GeminiConfig", "CRITICAL ERROR: GEMINI_API_KEY is EMPTY")
        }
    }

    suspend fun generateItinerary(prompt: String): String? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val modelName = "gemini-2.5-flash" // Atualizado conforme solicitado
        val endpoint = "v1beta/models/$modelName:generateContent"
        
        Log.d("GeminiRequest", "Generating itinerary with model: $modelName")
        Log.d("GeminiRequest", "Endpoint: $endpoint")
        Log.d("GeminiRequest", "Prompt: $prompt")
        
        if (apiKey.isEmpty()) {
            Log.e("GeminiRequest", "Aborting request: API Key is missing")
            return "Erro: Chave de API não configurada corretamente."
        }

        return try {
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = prompt)
                        )
                    )
                )
            )
            
            val response = apiService.generateContent(apiKey, request)
            val resultText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            
            if (resultText != null) {
                Log.d("GeminiResponse", "Success: ${resultText.take(50)}...")
                resultText
            } else {
                Log.e("GeminiResponse", "Empty response from Gemini")
                null
            }
        } catch (e: Exception) {
            Log.e("GeminiResponse", "Exception during Gemini call with model $modelName", e)
            null
        }
    }
}
