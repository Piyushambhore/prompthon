package com.jansaarthi.schemes.ai

import com.jansaarthi.schemes.models.ExplainResponse

/**
 * OpenAI-powered explanation provider.
 *
 * Stub implementation — ready for integration during the hackathon.
 *
 * When implemented this provider should:
 *   1. POST the notice text to the OpenAI Chat Completions API.
 *   2. Parse the structured JSON response.
 *   3. Validate that no information is invented beyond the source text.
 *   4. Return a validated [ExplainResponse].
 *
 * If the API call fails for any reason, the provider should throw
 * so the [ExplanationService] can fall through to the next provider.
 */
class OpenAIProvider(private val apiKey: String?) : AIProvider {

    override val name = "OpenAI"

    override fun isAvailable(): Boolean = !apiKey.isNullOrBlank()

    override suspend fun explain(text: String): ExplainResponse {
        // ─── TODO: Replace with actual OpenAI API call ─────────────
        //
        // val response = httpClient.post("https://api.openai.com/v1/chat/completions") {
        //     header("Authorization", "Bearer $apiKey")
        //     contentType(ContentType.Application.Json)
        //     setBody(buildPrompt(text))
        // }
        // return parseAndValidate(response)
        //
        // ───────────────────────────────────────────────────────────

        throw UnsupportedOperationException(
            "OpenAI integration not yet implemented — falling back to next provider"
        )
    }
}
