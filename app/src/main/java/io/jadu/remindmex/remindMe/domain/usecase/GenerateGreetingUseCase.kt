package io.jadu.remindmex.remindMe.domain.usecase

import io.jadu.remindmex.remindMe.domain.model.Content
import io.jadu.remindmex.remindMe.domain.model.GeminiRequest
import io.jadu.remindmex.remindMe.domain.model.Part
import io.jadu.remindmex.remindMe.domain.repository.GeminiApiService

class GenerateGreetingUseCase(
    private val apiService: GeminiApiService,
    private val apiKey: String
) {
    suspend fun execute(prompt: String): String {
        val request = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            )
        )
        val response = apiService.generateContent(apiKey, request)
        return response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response"
    }
}
