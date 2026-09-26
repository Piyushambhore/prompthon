package com.jansaarthi.schemes.services

import com.jansaarthi.schemes.ai.AIProvider
import com.jansaarthi.schemes.models.ExplainResponse

/**
 * Orchestrates official-notice explanation by delegating to
 * a chain of [AIProvider] implementations.
 *
 * Provider order matters — the first available provider that
 * succeeds wins.  [RuleBasedProvider] should always be last
 * as the guaranteed fallback.
 *
 *   ExplanationService
 *          ↓
 *      AIProvider
 *          ↓
 *    ┌─────┴──────┐
 *    │            │
 *  OpenAI   RuleBased
 */
class ExplanationService(private val providers: List<AIProvider>) {

    init {
        require(providers.isNotEmpty()) {
            "At least one AIProvider is required (use RuleBasedProvider as fallback)."
        }
    }

    suspend fun explain(text: String): ExplainResponse {
        for (provider in providers) {
            if (!provider.isAvailable()) continue

            try {
                return provider.explain(text)
            } catch (e: Exception) {
                // Safe log — never expose internals to the client
                println("Provider '${provider.name}' failed, trying next: ${e.message}")
            }
        }

        // Should never reach here if RuleBasedProvider is in the list
        throw IllegalStateException(
            "No AI provider could process the request. " +
            "Ensure RuleBasedProvider is included as a fallback."
        )
    }
}
